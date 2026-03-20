import argparse
import gzip
import ijson
from trie_hex import Trie, NodeType
from eth._utils import address
from eth_utils import to_canonical_address

def build_trie(input_file_path: str, trie_file_dump:str, print_stat: bool):

    unclassified_address = 0
    trie = Trie()
    trie.load_trie(trie_file_dump)

    with gzip.open(input_file_path, "rb") as file:

        print(f'Start building trie for file {input_file_path}')
        for index,transaction in enumerate(ijson.items(file, 'item.transactions.item')):
            #print(f'Parsing transaction {index}\r', end=' ')
            # If the destination address is None then it is a smart contract creation
            from_address = transaction.get('from').get('address')
            to_address = transaction.get('to').get('address')

            trie.add(from_address[2:], NodeType.EOA) # Add the EOA address to the trie

            if to_address is None:
                # Generate the smart contract address
                contract_address = address.generate_contract_address(
                    address=to_canonical_address(from_address),
                    nonce=int(transaction.get("nonce"), 16)
                )
                #Add the address to the trie       
                trie.add(contract_address.hex(), NodeType.SC)

            elif 'logs' in transaction:
                #Add the address to the trie       
                trie.add(to_address[2:], NodeType.SC) # Add the EOA address to the trie
            
            # Check if the address is not already in the trie, otherwise we overwrite the old type
            elif trie.find(to_address[2:]) is None:
                trie.add(to_address[2:], NodeType.UNK)
                unclassified_address += 1

    trie.save_trie(trie_file_dump)
    
    if print_stat:
        trie.print_stat()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="json splitter CLI")
    parser.add_argument('-i', '--input', required=True, help="Input file", type=str)
    parser.add_argument('-o', '--trie', required=False, help="Output trie dump file", type=str)
    parser.add_argument('-pt', '--print-stat', required=False, action="store_true",help="Print trie stats", default=False)
    args = parser.parse_args();

    build_trie(args.input, args.trie, args.print_stat)

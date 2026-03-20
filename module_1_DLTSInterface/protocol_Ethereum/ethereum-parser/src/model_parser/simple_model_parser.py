from file_splitter_helper import FileSplitterHelper
from abstract_model_parser import AbstractModelParser
from trie_hex import Trie
from distutils.dir_util import copy_tree

class SimpleModelParser(AbstractModelParser):

    def __init__(self, input_file_name:str, output_folder: str, max_file_size_mb: int, format: str) -> None:
        out_folder = f'{output_folder}/model2-data'
        dump_name = input_file_name.split('.')[0]
        self._account_splitter = FileSplitterHelper(f'{out_folder}/nodes/account-{dump_name}.{format}', max_file_size_mb, 'headers/model2/account_node_headers.csv')
        self._transfer_splitter = FileSplitterHelper(f'{out_folder}/rel/transfer-{dump_name}.{format}', max_file_size_mb, 'headers/model2/transfer_rel_headers.csv')
        self._invocation_splitter = FileSplitterHelper(f'{out_folder}/rel/invocation-{dump_name}.{format}', max_file_size_mb, 'headers/model2/invocation_rel_headers.csv')
        self._creation_splitter = FileSplitterHelper(f'{out_folder}/rel/creation-{dump_name}.{format}', max_file_size_mb, 'headers/model2/creation_rel_headers.csv')
        self._unk_rel_splitter = FileSplitterHelper(f'{out_folder}/rel/unk-{dump_name}.{format}', max_file_size_mb, 'headers/model2/unk_rel_headers.csv')

        copy_tree('headers/model2', f'{out_folder}/headers')

    def parse_block(self, block: dict):
        pass
    
    def parse_eoa_transaction(self, transaction: dict, block: dict):
        block = self._add_dict_prefix(dict=block,prefix='block')
        transaction = {**transaction, **block}
        self._transfer_splitter.append(element=transaction)

    def parse_contract_transaction(self, transaction: dict, block: dict):
        transaction = transaction.copy()
        block = self._add_dict_prefix(dict=block, prefix='block')
        transaction = {**transaction, **block}
        self._flatten_logs(transaction)
        self._invocation_splitter.append(element=transaction)

    def parse_contract_creation(self, transaction: dict, block: dict):
        transaction = transaction.copy()
        block = self._add_dict_prefix(dict=block,prefix='block')
        transaction = {**transaction, **block}
        self._flatten_logs(transaction)
        self._creation_splitter.append(element=transaction)

    def parse_unknown_transaction(self, transaction: dict, block: dict):
        block = self._add_dict_prefix(dict=block,prefix='block')
        transaction = {**transaction, **block}
        self._unk_rel_splitter.append(element=transaction)

    def parse_trie(self, trie: Trie):
        for key, item in trie.datrie.items():
            self._account_splitter.append(element={'address': f'0x{key}', 'account_type': item.value})

    def close_parser(self):
        self._account_splitter.end_file()
        self._transfer_splitter.end_file()
        self._invocation_splitter.end_file()
        self._creation_splitter.end_file()
        self._unk_rel_splitter.end_file()

        print("\nModel 2 (simple) stats:")
        print("- total address: ", self._account_splitter.total_row_saved)
        print("- total transfer transaction: ", self._transfer_splitter.total_row_saved)
        print("- total invocation transaction: ", self._invocation_splitter.total_row_saved)
        print("- total creation transaction: ", self._creation_splitter.total_row_saved)
        print("- total unk transaction: ", self._unk_rel_splitter.total_row_saved)

    def _add_dict_prefix(self, dict: dict, prefix: str):
        new_dict = {}
        for key in dict:
            new_dict[f'{prefix}_{key}'] = dict[key]
        
        return new_dict

    def _flatten_logs(self, transaction: dict):
        """
            This function flat the information of the logs in some parallel array
        """

        transaction['logs_address'] = []
        transaction['logs_topic'] = []
        transaction['logs_data'] = []
        transaction['logs_block_number'] = []
        transaction['logs_transaction_index'] = []
        transaction['logs_index'] = []
        transaction['logs_type'] = []
        transaction['logs_transaction_hash'] = []
        
        if 'logs' in transaction:
            logs = transaction['logs']
            del transaction['logs']

            for log in logs:
                transaction['logs_address'].append(log.get('address',''))
                # Non posso memorizzare array di array, ma so che i topics possono essere al massimo 3. 
                # Quindi li metto tutti nello stesso array parallelo, e quindi so che i primi 3 sono del primo log
                # i secondi 3 del secondo log e cosi via. Se trovo -1 non ho topic in quella posizione
                topics = ['-1','-1','-1', '-1']
                for index, topic in enumerate(log.get('topics', [])):
                    topics[index] = topic
                transaction['logs_topic'].append('|'.join(topics))
                transaction['logs_data'].append(log.get('data'))
                transaction['logs_block_number'].append(log.get('blockNumber',''))
                transaction['logs_transaction_index'].append(log.get('transactionIndex',''))
                transaction['logs_index'].append(log.get('logIndex',''))
                transaction['logs_type'].append(log.get('@type',''))
                transaction['logs_transaction_hash'].append(log.get('transactionHash',''))
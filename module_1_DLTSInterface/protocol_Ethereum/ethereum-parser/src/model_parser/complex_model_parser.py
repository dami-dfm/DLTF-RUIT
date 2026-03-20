from file_splitter_helper import FileSplitterHelper
from abstract_model_parser import AbstractModelParser
from trie_hex import Trie
from distutils.dir_util import copy_tree

class ComplexModelParser(AbstractModelParser):

    def __init__(self,input_file_name: str, output_folder: str, max_file_size_mb: int, format: str) -> None:
        out_folder = f'{output_folder}/model1-data'
        dump_name = input_file_name.split('.')[0]
        # NODES
        nodes_folder = f'{out_folder}/nodes'
        self._block_splitter = FileSplitterHelper(f'{nodes_folder}/blocks-{dump_name}.{format}', max_file_size_mb, 'headers/model1/block_node_headers.csv')
        self._transaction_splitter = FileSplitterHelper(f'{nodes_folder}/txs-{dump_name}.{format}', max_file_size_mb, 'headers/model1/txs_node_headers.csv')
        self._account_splitter = FileSplitterHelper(f'{nodes_folder}/account-{dump_name}.{format}', max_file_size_mb, 'headers/model1/account_node_headers.csv')
        self._log_splitter = FileSplitterHelper(f'{nodes_folder}/log-{dump_name}.{format}', max_file_size_mb, 'headers/model1/log_node_headers.csv')

        # REL
        rel_folder = f'{out_folder}/rel'
        self._sent_splitter = FileSplitterHelper(f'{rel_folder}/sent-{dump_name}.{format}', max_file_size_mb, 'headers/model1/sent_rel_headers.csv')
        self._contained_splitter = FileSplitterHelper(f'{rel_folder}/contained-{dump_name}.{format}', max_file_size_mb, 'headers/model1/contain_rel_headers.csv')
        self._block_child_splitter = FileSplitterHelper(f'{rel_folder}/child-of-{dump_name}.{format}', max_file_size_mb, 'headers/model1/block_child_rel_headers.csv')
        self._transfer_splitter = FileSplitterHelper(f'{rel_folder}/transfer-{dump_name}.{format}', max_file_size_mb, 'headers/model1/transfer_rel_headers.csv')
        self._creation_splitter = FileSplitterHelper(f'{rel_folder}/creation-{dump_name}.{format}', max_file_size_mb, 'headers/model1/creation_rel_headers.csv')
        self._invocation_rel_splitter = FileSplitterHelper(f'{rel_folder}/invocation-{dump_name}.{format}', max_file_size_mb, 'headers/model1/invocation_rel_headers.csv')
        self._emitted_splitter = FileSplitterHelper(f'{rel_folder}/emitted-{dump_name}.{format}', max_file_size_mb, 'headers/model1/log_rel_headers.csv')
        self._unk_rel_splitter = FileSplitterHelper(f'{rel_folder}/unk-{dump_name}.{format}', max_file_size_mb, 'headers/model1/unk_rel_headers.csv')

        copy_tree('headers/model1', f'{out_folder}/headers')

    def parse_block(self, block: dict):
        self._block_splitter.append(element=block)
        self._block_child_splitter.append(element={'block_hash': block['hash'], 'parent_block_hash': block['parentHash']})
    
    def parse_eoa_transaction(self, transaction: dict):
        self._sent_splitter.append(element={'account_address': transaction['fromAddress'], 'txs_hash': transaction['hash']})
        self._contained_splitter.append(element={'txs_hash': transaction['hash'], 'block_hash': transaction['blockHash']})
        self._transaction_splitter.append(element=transaction)
        self._transfer_splitter.append(element={'txs_hash': transaction['hash'], 'account_address': transaction['toAddress']})

    def parse_contract_transaction(self, transaction: dict):
        transaction = transaction.copy()
        logs = transaction.get('logs', [])
        if 'logs' in transaction:
            del transaction['logs']
        self._sent_splitter.append(element={'account_address': transaction['fromAddress'], 'txs_hash': transaction['hash']})
        self._contained_splitter.append(element={'txs_hash': transaction['hash'], 'block_hash': transaction['blockHash']})
        self._transaction_splitter.append(element=transaction)
        self._invocation_rel_splitter.append(element={'txs_hash': transaction['hash'], 'account_address': transaction['toAddress']})
        self._parse_logs(logs=logs, transaction_hash=transaction['hash'])

    def parse_contract_creation(self, transaction:dict):
        transaction = transaction.copy()
        logs = transaction.get('logs', [])
        if 'logs' in transaction:
            del transaction['logs']
        self._sent_splitter.append(element={'account_address': transaction['fromAddress'], 'txs_hash': transaction['hash']})
        self._contained_splitter.append(element={'txs_hash': transaction['hash'], 'block_hash': transaction['blockHash']})
        self._transaction_splitter.append(element=transaction)
        self._creation_splitter.append(element={'txs_hash': transaction['hash'], 'account_address': transaction['contractAddress']})
        self._parse_logs(logs=logs, transaction_hash=transaction['hash'])

    def parse_unknown_transaction(self, transaction: dict):
        self._sent_splitter.append(element={'account_address': transaction['fromAddress'], 'txs_hash': transaction['hash']})
        self._contained_splitter.append(element={'txs_hash': transaction['hash'], 'block_hash': transaction['blockHash']})
        self._transaction_splitter.append(element=transaction)
        self._unk_rel_splitter.append(element={'txs_hash': transaction['hash'], 'account_address': transaction['toAddress']})

    def _parse_logs(self, logs, transaction_hash):
        for index, log in enumerate(logs):
            log_hash = f'{index}_{transaction_hash}'
            log['hash'] = log_hash
            if 'topics' not in log:
                log['topics'] = ['-1']
            self._log_splitter.append(element=log)
            self._emitted_splitter.append(element={'txs_hash': transaction_hash,'log_hash': log_hash})

    def parse_trie(self, trie: Trie):
        for key, item in trie.datrie.items():
            self._account_splitter.append(element={'address': f'0x{key}', 'account_type': item.value})

    def close_parser(self):
        # Safe close all splitter
        self._block_splitter.end_file()
        self._transaction_splitter.end_file()
        self._account_splitter.end_file()
        self._sent_splitter.end_file()
        self._contained_splitter.end_file()
        self._transfer_splitter.end_file()
        self._creation_splitter.end_file()
        self._invocation_rel_splitter.end_file()
        self._emitted_splitter.end_file()
        self._unk_rel_splitter.end_file()

        print("\nModel 1 (complex) stats:")
        print("- total blocks: ", self._block_splitter.total_row_saved)
        print("- total transactions: ", self._transaction_splitter.total_row_saved)
        print("- total address: ", self._account_splitter.total_row_saved)
        print("- total logs: ", self._log_splitter.total_row_saved)
        print("- total unknown transactions: ", self._unk_rel_splitter.total_row_saved)
from abc import ABC, abstractmethod

class AbstractModelParser(ABC):

    @abstractmethod
    def parse_block(self, block: dict):
        pass

    @abstractmethod
    def parse_eoa_transaction(self, transaction: dict, block: dict):
        pass

    @abstractmethod
    def parse_contract_transaction(self, transaction: dict, block:dict):
        pass
    
    @abstractmethod
    def parse_contract_creation(self, transaction: dict, block: dict):
        pass

    @abstractmethod
    def parse_unknown_transaction(self, transaction: dict, block: dict):
        pass


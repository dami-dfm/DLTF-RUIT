import requests
import json
import os

class EthereumClient:

    BASE_URL = 'https://palpable-old-gadget.quiknode.pro'
    HEADERS = {'ContentType': 'application/json'}

    def __init__(self) -> None:
        self.tot_requests = 0
        self.avg_response_time = 0

    def _send_request(self, payload):
        self.tot_requests += 1
        response = requests.request(method="POST", url=f'{self.BASE_URL}/{os.environ["ETH_CLIENT_TOKEN"]}/', headers=self.HEADERS, data=payload)

        if self.tot_requests == 1:
            self.avg_response_time = response.elapsed.seconds
        else: 
            self.avg_response_time = (self.avg_response_time + response.elapsed.seconds) / 2
        return response
    
    def eth_getCode(self, address: str):
        payload = json.dumps({
            "jsonrpc": "2.0",
            "id": 1,
            "method": "eth_getCode",
            "params": [
                address,
                "latest"
            ]
        })

        response = self._send_request(payload=payload)
        return response.json()
    
    def eth_getAccount(self, address: str):
        payload = json.dumps({
            "jsonrpc": "2.0",
            "id": 1,
            "method": "eth_getAccount",
            "params": [
                address,
                "latest"
            ]
        })

        response = self._send_request(payload=payload)
        return response.json()
    
    def is_contract(self, address: str):

        code_response = self.eth_getCode(address=address)

        if code_response['result'] != '0x':
            return True

        else:
            print(f"No code found for {address}, calling eth_getAccount")
            account_response = self.eth_getAccount(address=address)
            
            # If no info are present in the latest block for the address, it means it is a SC self destructed
            return account_response['result'] is None
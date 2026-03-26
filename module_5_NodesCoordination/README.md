![PNRR2022banner](../banner.png)
# On demand decentralized oracles Chainlink based architecture for blockchain
This repository contains the implementation of a Chainlink-based flexible and reliable oracle system for on-demand objective data requests.


### Deploy (Hardhat + Sepolia)
This repo originally used Truffle + a local Geth chain; for Sepolia you can deploy the same contracts with Hardhat.

From `/chainlink_truffle`:
```sh
npx hardhat compile
# Set env vars in .env (see .env.example), then:
npx hardhat run scripts/deploy-system.js --network sepolia
```

This writes the deployed addresses to `/chainlink_truffle/deployments/system.sepolia.json`.

Notes:
- OCR requires `OCR_ENCODED_CONFIG`; you can either set it directly, or set `OCR_CONFIG_FILE`/`OCR_CONFIG_PRESET` and the deploy script will generate it via `chainlink_go/core/internal/encodeGenerator.go`.

### Legacy (Truffle)
If you still want to use the original Truffle migrations, you can keep using:
```sh
truffle migrate -f 2 --to 3 --network geth
```


## *_encoded* field generation tool
During the deployment of OCR-SC it is necessary to produce a piece of data to be associated with the *_encoded* parameter when calling the *setConfig* function. So we created our own tool by reverse engineering the Chainlink core test files. /chainlink_go/core/internal/encodeGenerator.go).

![PNRR2022banner](../banner.png)
# Distributed Ledger Graph Framework

This project provides a unified framework for representing, storing, and querying heterogeneous Distributed Ledger Technology (DLT) data using graph-based models and efficient compression techniques.

## Overview

We introduce a common data format and a graph-based abstraction of distributed ledgers, enabling consistent analysis across different DLT protocols. The system combines compact graph storage, efficient retrieval, and semantic enrichment through database integration.

## Key Features

- **Unified Data Format**  
  A common representation for heterogeneous DLT datasets, allowing algorithms to be implemented once and reused across different protocols.

- **Graph-Based Ledger Model**  
  A theoretical model called the **Payment Graph**, representing ledgers as labeled temporal graphs with potentially heterogeneous entities and relationships.

- **Efficient Compression & Storage**  
  Utilizes the WebGraph Framework to:
  - Compress large-scale graph data
  - Exploit structural similarities and temporal locality
  - Enable constant-time (`O(1)`) access per vertex

- **Semantic Enrichment with Neo4j**  
  Stores additional metadata (e.g., transaction amounts, timestamps) in a graph database, complementing the structural representation.

- **Containerized Deployment**  
  Fully containerized using Docker for easy deployment and integration into larger systems.

## Architecture

The system consists of two main components:

1. **WebGraph-Based Component**
   - Handles compressed storage of the Payment Graph
   - Provides efficient in-memory access and reconstruction

2. **Neo4j Database**
   - Stores semantic and attribute-level information
   - Enables advanced querying and analysis

Both components are packaged as Docker services and can be orchestrated via Docker Compose.

## Deliverables

The project includes the following modules:

- **MD1 – Data Indexing and Storage Module**
- **MD2 – Graph-Based Ledger Representation**
- **MD2 – Compression Scheme for Ledger Data**

## Methodology

### 1. Unified Data Representation
Different DLT systems rely on distinct data models. We defined a shared format that abstracts these differences, enabling uniform processing.

### 2. Payment Graph Model
Ledgers are modeled as labeled temporal graphs where:
- Nodes represent entities
- Edges represent time-dependent interactions
- Labels encode roles and attributes

### 3. Compression Strategy
We designed algorithms that:
- Exploit temporal locality
- Leverage structural similarities
- Encode graphs efficiently using WebGraph

### 4. Data Retrieval
An efficient retrieval mechanism:
- Reconstructs the graph from compressed data
- Supports targeted queries on the in-memory representation

### 5. Semantic Integration
Neo4j is used to:
- Store transaction metadata
- Enable hybrid structural + semantic queries

## Deployment

The system is fully containerized using Docker.

### Requirements

- Docker
- Docker Compose

### Running the System

```bash
docker-compose up --build

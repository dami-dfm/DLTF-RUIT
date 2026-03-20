# Ledger Querying, Community Detection, and Anomaly Analysis

This module extends the Distributed Ledger Graph Framework with advanced querying, community detection, and anomaly detection capabilities over large-scale temporal graphs.

## Overview

Building on the graph representation and storage infrastructure, this component introduces efficient algorithms for:

- Parameterized querying and filtering
- Temporal pattern extraction
- Community detection at scale
- Automated anomaly detection

It also enhances interoperability by exposing core functionality across multiple programming environments.

## Key Features

- **Parameterized Querying**
  - Extract subgraphs and patterns based on user-defined criteria
  - Support for temporal and heterogeneous constraints

- **Temporal Pattern Recognition**
  - Identification of structures constrained by time windows
  - Detection of evolving relationships among entities

- **Scalable Community Detection**
  - Rust implementation of the **Louvain algorithm**
  - Integrated into the WebGraph encoding pipeline
  - Capable of decomposing large graphs efficiently

- **Anomaly Detection**
  - Detection of unusual patterns using:
    - Temporal centrality measures
    - Distance-based statistics
    - Community structure analysis

- **Interoperability**
  - C-compatible shared library for cross-language usage
  - Integration with **igraph** for advanced graph analytics

## Architecture

The system integrates multiple components:

### 1. Query and Analysis Engine
- Enables filtering and extraction of subgraphs
- Supports temporal constraints and pattern matching

### 2. Community Detection Module
- Louvain algorithm implemented in Rust
- Scalable to large datasets (e.g., ~200,000 communities in ~5 hours)

### 3. Anomaly Detection Module
- Combines structural and temporal metrics
- Leverages community structures to detect irregularities

### 4. Interoperability Layer
- **C-compatible shared library**
- **igraph bindings** for reuse of existing algorithms

### 5. Neo4j Integration
- Supports loading and exploration of real-world datasets
- Provides both UI and programmatic access

## Deliverables

- **Data Retrieval and Elaboration Module**
- **Automated Data Inspection Tools**
  - Community detection
  - Anomaly detection

## Performance

- Processes large-scale graphs efficiently
- Example:
  - ~200,000 communities extracted from the Payments Graph
  - ~5 hours on a modest server

## Dataset

A sample dataset is available:

- **Bitcoin Payments Graph**  
  Download: https://drive.google.com/file/d/1G6HcLj-KEn1Rv4i_jY5ct27DScYek6n_/view?usp=drive_link

### Setup

1. Download the archive
2. Extract it into the `neo4j/` subfolder

## Deployment

The system is deployed via Docker Compose as a set of coordinated services.

### Services

- **Neo4j Web Interface**  
  http://localhost:8800/neo4j/browser/  
  Explore the raw graph interactively

- **Neo4j REST API**  
  http://localhost:8800/neo4j/db/paymentsgraph/tx/commit  
  Programmatic access to graph data

- **WebGraph Visualization Interface**  
  http://localhost:8800/  
  Interactive exploration of compressed graphs

## Features of the Web Interface

- Graph traversal strategies:
  - Breadth-First Search (BFS)
  - Depth-First Search (DFS)

- Dynamic visualization:
  - Asynchronous subgraph expansion
  - Start/stop graph population interactively

- Layout rendering:
  - Powered by **Graphviz**
  - Compiled to WebAssembly for performance

## Interoperability

Two auxiliary projects are included:

1. **C Shared Library**
   - Exposes the Rust Louvain implementation
   - Enables usage from external languages

2. **igraph Interface**
   - Provides bindings to exported functions
   - Allows reuse of established graph algorithms

## Use Cases

- Temporal graph querying
- Community evolution analysis
- Fraud and anomaly detection in ledgers
- Cross-platform graph analytics

## Future Work

- Optimization of temporal query execution
- Real-time anomaly detection
- Extension to additional datasets and protocols

## License

[Specify your license here]

## Authors

[Add authors or contributors here]

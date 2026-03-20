// USE FOR DELETE
:auto match (n) call {with n detach delete n} in transactions of 100 rows;

// CONSTRAINT
CREATE CONSTRAINT IF NOT EXISTS FOR (a: Account) REQUIRE a.address IS UNIQUE;

// Transaction hash index
CREATE INDEX transaction_rel_index IF NOT EXISTS FOR ()-[r: TO]-() ON (r.hash);
CREATE INDEX transaction_rel_index2 IF NOT EXISTS FOR ()-[r: CREATED]-() ON (r.hash);
CREATE INDEX transaction_rel_index3 IF NOT EXISTS FOR ()-[r: INVOKED]-() ON (r.hash);
CREATE INDEX transaction_rel_index4 IF NOT EXISTS FOR ()-[r: TRANSFERRED]-() ON (r.hash);

// Transaction block hash index
CREATE INDEX transaction_block_rel_index IF NOT EXISTS FOR ()-[r: TO]-() ON (r.block_hash);
CREATE INDEX transaction_block_rel_index IF NOT EXISTS FOR ()-[r: CREATED]-() ON (r.block_hash);
CREATE INDEX transaction_block_rel_index IF NOT EXISTS FOR ()-[r: INOVKED]-() ON (r.block_hash);
CREATE INDEX transaction_block_rel_index IF NOT EXISTS FOR ()-[r: TRANSFERRED]-() ON (r.block_hash);


// QUERY 1, EOA TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model2-data/eoa-transactions/eoa-transactions-1.csv" AS transaction
call {
    WITH transaction

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE 
        SET fromNode.account_type = 'EOA'

    MERGE (toNode: Account {address: transaction.toAddress})
    ON CREATE
        SET toNode.account_type = 'EOA'


    CREATE (fromNode)-[r:transfered]->(toNode)
    SET r = properties(transaction)
} IN TRANSACTIONS OF 10000 ROWS;

// QUERY 2, CONTRACT CREATION TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model2-data/contract-creation/contract-creation-1.csv" AS transaction
call {
    WITH transaction

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE
        SET fromNode.account_type = 'EOA'

    MERGE (contract: Account {address: transaction.contractAddress})
    ON CREATE
        SET contract.account_type = 'SC'

    CREATE (fromNode)-[r:created]->(contract)
    SET r = properties(transaction)
    SET r.logs_topic = SPLIT(transaction.logs_topic,';')
    SET r.logs_index = SPLIT(transaction.logs_index,';')
    SET r.logs_data = SPLIT(transaction.logs_data,';')
    SET r.logs_block_number = SPLIT(transaction.logs_block_number,';')
    SET r.logs_transaction_index = SPLIT(transaction.logs_transaction_index,';')
    SET r.logs_index = SPLIT(transaction.logs_index,';')
    SET r.logs_type = SPLIT(transaction.logs_type,';')
    SET r.logs_type = SPLIT(transaction.logs_index,';')
    SET r.logs_transaction_hash = SPLIT(transaction.logs_transaction_hash,';')
} IN TRANSACTIONS OF 10000 ROWS;

// QUERY 3, CONTRACT INVOCATION TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model2-data/contract-transactions/contract-transactions-1.csv" AS transaction
call {
    WITH transaction

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE 
        SET fromNode.account_type = 'EOA'

    MERGE (contract: Account {address: transaction.toAddress})
    ON CREATE 
        SET contract.account_type = 'SC'

    CREATE (fromNode)-[r:invoked]->(contract)
    SET r = properties(transaction)
    SET r.logs_index = SPLIT(transaction.logs_index,';')
    SET r.logs_data = SPLIT(transaction.logs_data,';')
    SET r.logs_block_number = SPLIT(transaction.logs_block_number,';')
    SET r.logs_transaction_index = SPLIT(transaction.logs_transaction_index,';')
    SET r.logs_index = SPLIT(transaction.logs_index,';')
    SET r.logs_type = SPLIT(transaction.logs_type,';')
    SET r.logs_type = SPLIT(transaction.logs_index,';')
    SET r.logs_transaction_hash = SPLIT(transaction.logs_transaction_hash,';')

    // split by , resplit by ; and save the result as collection of collections. Not supported by neo4j
    // WITH transaction, SPLIT(transaction.logs_topic,',') AS topics
    //     UNWIND topics as topic
    //     WITH transaction, SPLIT(topic, ';') as subtopics
    //         WITH transaction, COLLECT(subtopics) as subtopic
    //             MERGE (fromNode)-[r:invoked]->(contract)
    //             SET r.logs_topic = COALESCE(r.logs_topic, []) + subtopic
    SET r.logs_topic = SPLIT(transaction.logs_topic,',')

} IN TRANSACTIONS OF 10000 ROWS;

// QUERY 4, UNKNOWN TRANSACTIONS
:auto LOAD CSV WITH HEADERS FROM "file:///output/model2-data/unknown-transactions/unknown-transactions-1.csv" AS transaction
call {
    WITH transaction

    MERGE (fromNode: Account {address: transaction.fromAddress})
    ON CREATE
        SET fromNode.account_type = "EOA"

    MERGE (toNode: Account {address: transaction.toAddress})
    ON CREATE
        SET toNode.account_type = "UNK"

    WITH transaction, fromNode, toNode,
    CASE
        WHEN toNode.account_type = "UNK" THEN "to"
        WHEN toNode.account_type = "EOA" THEN "transfered"
        WHEN toNode.account_type = "SC"  THEN "invoked"
    END AS relationType
    call apoc.create.relationship(fromNode,relationType,{},toNode) YIELD rel
    //call apoc.merge.relationship(fromNode,relationType,{},{},toNode,{}) YIELD rel // Avoid to create multiple times same relationship

    SET rel = properties(transaction)
    SET rel.logs_index = SPLIT(transaction.logs_index,';')
    SET rel.logs_data = SPLIT(transaction.logs_data,';')
    SET rel.logs_block_number = SPLIT(transaction.logs_block_number,';')
    SET rel.logs_transaction_index = SPLIT(transaction.logs_transaction_index,';')
    SET rel.logs_index = SPLIT(transaction.logs_index,';')
    SET rel.logs_type = SPLIT(transaction.logs_type,';')
    SET rel.logs_type = SPLIT(transaction.logs_index,';')
    SET rel.logs_transaction_hash = SPLIT(transaction.logs_transaction_hash,';')
    SET rel.logs_topic = SPLIT(transaction.logs_topic,';')

} IN TRANSACTIONS OF 1000 ROWS;





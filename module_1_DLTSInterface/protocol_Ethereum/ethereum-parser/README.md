# Etehreum parser
This tool parse json file full of ethereum transaction and split them in different file based on
the destination address of the transaction.

## Build and run

### Environment
Create a `.env` file in the `src` directory, using `.env.example` as a template or manually pass the env variable to the script.

### Run trie builder
Build the trie used by the parser for address classification

```bash
python trie_builder.py \
--input /data/backup/eth/blocks/output_0-999999.json.gz \
--output trie_dump/trie.trie \
--print-stat
```

### Run parser
Generate the csv for both model A and model B for neo4j import

```bash
python json_splitter.py \
-i /run/media/daniele/Dati/Università/Informatica/Tesi/eth_dumps/dump0_0-999999.json.gz \
-o ./output \
-s -1 \
--format csv \
--start-block 0 \
--end-block 2000000 \
--trie-path trie_dump/trie.trie
```

## neo4j-admin import
For full import change incremental argument 'incremental' with 'full' and remove --force --stage

### model1 import example
```bash
bin/neo4j-admin database import full \
--verbose \
--delimiter="," \
--array-delimiter=";" \
--skip-duplicate-nodes \
--skip-bad-relationships \
--report-file=report-model1.txt \
--nodes=Block=import/output/model1-data/headers/block_node_headers.csv,import/output/model1-data/nodes/blocks.\* \
--nodes=Account=import/output/model1-data/headers/account_node_headers.csv,import/output/model1-data/nodes/account.\* \
--nodes=Transaction=import/output/model1-data/headers/txs_node_headers.csv,import/output/model1-data/nodes/txs.\* \
--nodes=Log=import/output/model1-data/headers/log_node_headers.csv,import/output/model1-data/nodes/log.\* \
--relationships=SENT=import/output/model1-data/headers/sent_rel_headers.csv,import/output/model1-data/rel/sent.\* \
--relationships=CREATED=import/output/model1-data/headers/creation_rel_headers.csv,import/output/model1-data/rel/creation.\* \
--relationships=EMITTED=import/output/model1-data/headers/log_rel_headers.csv,import/output/model1-data/rel/emitted.\* \
--relationships=INVOKED=import/output/model1-data/headers/invocation_rel_headers.csv,import/output/model1-data/rel/invocation.\* \
--relationships=TRANSFERRED=import/output/model1-data/headers/transfer_rel_headers.csv,import/output/model1-data/rel/transfer.\* \
--relationships=TO=import/output/model1-data/headers/unk_rel_headers.csv,import/output/model1-data/rel/unk.\* \
--relationships=CONTAINED=import/output/model1-data/headers/contain_rel_headers.csv,import/output/model1-data/rel/contained.\* \
--relationships=CHILD_OF=import/output/model1-data/headers/block_child_rel_headers.csv,import/output/model1-data/rel/child-of.\* \
model1
```

### model2 import example
```bash
bin/neo4j-admin database import full \
--verbose \
--delimiter="," \
--array-delimiter=";" \
--skip-duplicate-nodes \
--overwrite-destination=true \
--report-file=report-model2.txt \
--nodes=Account=import/output/model2-data/headers/account_node_headers.csv,import/output/model2-data/nodes/account.\* \
--relationships=CREATED=import/output/model2-data/headers/creation_rel_headers.csv,import/output/model2-data/rel/creation.\* \
--relationships=INVOKED=import/output/model2-data/headers/invocation_rel_headers.csv,import/output/model2-data/rel/invocation.\* \
--relationships=TRANSFERRED=import/output/model2-data/headers/transfer_rel_headers.csv,import/output/model2-data/rel/transfer.\* \
--relationships=TO=import/output/model2-data/headers/unk_rel_headers.csv,import/output/model2-data/rel/unk.\* \
model2
```

## Query Cypher


<table>
<thead>
<tr> 
    <th>#</th>
    <th style="width: 10px">Descrizione</th>
    <th>Model A</th>
    <th>Model B</th>
</tr>
</thead>

<tbody>
<tr>
<td>Q1</td>
<td>
Conta le transazioni di qualsiasi tipo
</td>
<td> <pre>
MATCH 
    (n: Transaction) 
RETURN 
    COUNT(*)
</pre> </td>
<td> <pre>
MATCH
    (s:Account)-[r]->(d) 
RETURN 
    COUNT(*)
</pre> </td>
</tr>

<tr>
<td>Q2</td>
<td>
Conta tutte le transazioni che hanno trasferito 0 ether
</td>
<td> <pre>
MATCH 
    (n: Transaction)
WHERE 
    n.value = 0 
RETURN 
    COUNT(*)
</pre> </td>
<td> <pre>
MATCH
    (s:Account)-[r]->(d)
WHERE 
    r.value = 0 
RETURN 
    COUNT(*)
</pre> </td>
</tr>

<tr>
<td>Q3</td>
<td>
Selezionare tutte le transazioni dell'account con indirizzo 0x54daeb3e8a6bbc797e4ad2b0339f134b186e4637
</td>
<td> <pre>
MATCH
    (a:Account)-[:SENT]->(t:Transaction)
WHERE 
    a.address = "0x54daeb3e8a6bbc797e4ad2b0339f134b186e4637"
RETURN t
</pre> </td>
<td> <pre>
MATCH
    (a:Account {address: "0x54daeb3e8a6bbc797e4ad2b0339f134b186e4637"})-[r]->()
RETURN r
</pre> </td>
</tr>

<tr>
<td>Q4</td>
<td>
Conta gli account che hanno effettuato trasferimenti verso se stessi (loop nel grafo)
</td>
<td> <pre>
MATCH
    (a:Account)-[r:SENT]->(t: Transaction)-[r2]->(b: Account {address: a.address})
RETURN 
    COUNT(r)
</pre> </td>
<td> <pre>
MATCH
    (a:Account)-[r]->(b: Account {address: a.address})
RETURN
    COUNT(r)
</pre> </td>
</tr>

<tr>
<td>Q5</td>
<td>
Trova tutti gli account che non hanno fatto nessuna transazione, ma che sono solo destinatari di trasferimenti
</td>
<td> <pre>
MATCH 
    (account: Account)
WHERE 
    NOT (account)-[:SENT]-(:Transaction) 
RETURN 
    account
</pre> </td>
<td> <pre>
MATCH 
    (account:Account) 
WHERE NOT 
    (account)-[]->(:Account) 
RETURN
    account
</pre> </td>
</tr>

<tr>
<td>Q6</td>
<td>
Numero medio di transazioni effettuate da ogni account
</td>
<td> <pre>
MATCH 
    (account:Account)-[:SENT]->(t:Transaction)-[:TRANSFERRED]->()
WITH 
    account,
    COUNT(t) as numTransazioni
RETURN 
    AVG(numTransazioni) AS mediaTransazioni
</pre> </td>
<td> <pre>
MATCH
    (a:Account)-[:TRANSFERRED]->(:Account)
WITH 
    a,
    COUNT(*) as numTransazioni
RETURN 
    AVG(numTransazioni) AS mediaTransazioni;
</pre> </td>
</tr>

<tr>
<td>Q7</td>
<td>
Conta il numero di transazioni che hanno emesso l'evento con firma 0xf63780e752c6a54a94fc52715dbc5518a3b4c3c2833d301a204226548a2a8545
</td>
<td> <pre>
MATCH 
    (e:Event)
WHERE
    ANY(topic in e.topics where topic = '0xf63780e752c6a54a94fc52715dbc5518a3b4c3c2833d301a204226548a2a8545')
RETURN COUNT(*);
</pre> </td>
<td> <pre>
MATCH 
    (n)-[r]->(c)
WHERE 
    ANY(topic in r.logs_topic WHERE topic =~ '.*0xf63780e752c6a54a94fc52715dbc5518a3b4c3c2833d301a204226548a2a8545.*') 
RETURN 
    COUNT(*)
</pre> </td>
</tr>

<tr>
<td>Q8</td>
<td>
Ritorna tutte le transazioni del blocco con hash 0x890158751fc766ad90d9abebde93830f66e8d503a99f5d25c8dbf8bffeeba388
</td>
<td> <pre>
MATCH 
    (t:Transaction)-[:CONTAINED]->(b:Block {hash: '0x890158751fc766ad90d9abebde93830f66e8d503a99f5d25c8dbf8bffeeba388'})
RETURN 
    t;
</pre> </td>
<td> <pre>
MATCH 
    (a:Account)-[r]->(b) 
WHERE
    r.block_hash = '0x890158751fc766ad90d9abebde93830f66e8d503a99f5d25c8dbf8bffeeba388' 
RETURN 
    r;
</pre> </td>
</tr>

<tr>
<td>Q9</td>
<td>
Numero medio di transazioni per blocco
</td>
<td> <pre>
MATCH 
    (b:Block)<-[:CONTAINED]-(t:Transaction)
WITH 
    b,
    COUNT(t) AS numTransazioni
RETURN 
    AVG(numTransazioni)
</pre> </td>
<td> <pre>
MATCH 
    (a)-[t]->(b)
WITH 
    t.blockHash as block,
    COUNT(t) as numT
RETURN
    AVG(numT)
</pre> </td>
</tr>

<tr>
<td>Q10</td>
<td>
Conta tutte le transazioni dirette verso l'indirizzo nullo 0x0000000000000000000000000000000000000000
</td>
<td> <pre>
MATCH 
    (t:Transaction)-[r]->(n:Account {address: '0x0000000000000000000000000000000000000000'})
RETURN 
    COUNT(*);
</pre> </td>
<td> <pre>
MATCH 
    (a: Account)-[t]->(b: Account) 
WHERE 
    b.address = '0x0000000000000000000000000000000000000000'
RETURN 
    COUNT(*);
</pre> </td>
</tr>

<tr>
<td>Q11</td>
<td>
Ritorna per ogni blocco, il numero di transazioni al suo interno
</td>
<td> <pre>
MATCH 
    (block:Block)<-[:CONTAINED]-(transaction:Transaction)
WITH 
    block,
    COUNT(transaction) AS numTransactions
ORDER BY numTransactions DESC
RETURN 
    block,
    numTransactions
</pre> </td>
<td> <pre>
MATCH 
    ()-[t]->()
WITH 
    t.block_hash AS blockHash,
    COUNT(t) AS numTransactions
RETURN 
    blockHash,
    numTransactions
ORDER by numTransactions DESC
</pre> </td>
</tr>

<tr>
<td>Q12</td>
<td>
Media numero nodi vicini di ogni EOA
</td>
<td> <pre>
MATCH 
    (a:Account)-[:SENT]-(t:Transaction)-[:TRANSFERRED]->(b)
WITH 
    b,
    COUNT(b) AS numNeighbors
RETURN
    AVG(numNeighbors)
</pre> </td>
<td> <pre>
MATCH 
    (a:Account)-[:TRANSFERRED]->(b)
WITH 
    b,
    COUNT(b) AS numNeighbors
RETURN
    AVG(numNeighbors)
</pre> </td>
</tr>

<tr>
<td>Q13</td>
<td>
Conta i vicini Account di secondo grado (2 hop di distanza) dei primi 100 nodi che hanno fatto piu transazioni verso EOA
</td>
<td> <pre>
MATCH 
    (n:Account)-[r:SENT]-(t:Transaction)-[:TRANSFERRED]->(b:Account)
WHERE 
    n <> b
WITH 
    n,
    COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 100
MATCH 
    (n)-[*4]->(neighbor)
WHERE 
    neighbor <> n and (neighbor: Account)
RETURN 
    COUNT(neighbor);
</pre> </td>
<td> <pre>
MATCH 
    (n:Account)-[r:TRANSFERRED]->(b:Account)
WHERE n <> b
WITH n, COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 100
MATCH 
    (n)-[*2]->(neighbor)
WHERE 
    neighbor <> n and (neighbor: Account)
RETURN 
    COUNT(neighbor);
</pre> </td>
</tr>

<tr>
<td>Q14</td>
<td>
Conta i vicini Account di terzo grado (3 hop di distanza) dell’account che ha effettuato piu transaction verso EOA
</td> <td> <pre>
MATCH 
    (n:Account)-[r:SENT]-(t:Transaction)-[:TRANSFERRED]->(b:Account)
WHERE n <> b
WITH 
    n,
    COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 1
MATCH 
    (n)-[*6]->(neighbor)
WHERE 
    neighbor <> n and (neighbor: Account)
RETURN 
    COUNT(neighbor);
</pre> </td>
<td> <pre>
MATCH 
    (n:Account)-[r:TRANSFERRED]->(b:Account)
WHERE 
    n <> b
WITH 
    n,
    COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 1
MATCH 
    (n)-[*3]->(neighbor)
WHERE 
    neighbor <> n and (neighbor: Account)
RETURN 
    COUNT(neighbor);
</pre> </td>
</tr>

<tr>
<td>Q15</td>
<td>
Conta i vicini Account di terzo grado (3 hop di distanza) dell’account 0x32be343b94f860124dc4fee278fdcbd38c102d88
</td> <td> <pre>
MATCH
    (a:Account {address: '0x32be343b94f860124dc4fee278fdcbd38c102d88'})
MATCH 
    (a)-[*6]->(neighbor: Account)
WHERE 
    a <> neighbor
RETURN 
    count(neighbor)
</pre> </td>
<td> <pre>
MATCH 
    (a:Account {address: '0x32be343b94f860124dc4fee278fdcbd38c102d88'})
MATCH 
    (a)-[*3]->(neighbor: Account)
WHERE a <> neighbor
RETURN 
    COUNT(neighbor)
</pre> </td>
</tr>

<tr>
<td>Q16</td>
<td>
Conta i vicini Account di quarto grado dell’account 0x32be343b94f860124dc4fee278fdcbd38c102d88
</td> <td> <pre>
MATCH
    (a:Account {address: '0x32be343b94f860124dc4fee278fdcbd38c102d88'})
MATCH 
    (a)-[*8]->(neighbor: Account)
WHERE 
    a <> neighbor
RETURN 
    count(neighbor)
</pre> </td>
<td> <pre>
MATCH 
    (a:Account {address: '0x32be343b94f860124dc4fee278fdcbd38c102d88'})
MATCH 
    (a)-[*4]->(neighbor: Account)
WHERE a <> neighbor
RETURN 
    COUNT(neighbor)
</pre> </td>
</tr>

<tr>
<td>Q17</td>
<td>
Ritorna tutte le transaction vicine della transazione con hash 0x4b1ebc387227fedfb5753134e9009a3e2aceb83e86aa4df12dc5531984447d34
</td> <td> <pre>
MATCH
    (t:Transaction {hash: '0x4b1ebc387227fedfb5753134e9009a3e2aceb83e86aa4df12dc5531984447d34'})-[]->(a: Account)
MATCH 
    (a)-[:SENT]->(t1: Transaction)
RETURN t1
</pre> </td>
<td> <pre>
MATCH 
    (a:Account)-[r {hash: '0x4b1ebc387227fedfb5753134e9009a3e2aceb83e86aa4df12dc5531984447d34'}]->(b:Account)
MATCH
    (b)-[r2]->()
RETURN r2
</pre> </td>
</tr>

<tr>
<td>Q18</td>
<td>
Ritorna tutte le transazioni vicine di tutte le transazioni effettuate dall'account 0x32be343b94f860124dc4fee278fdcbd38c102d88 
</td> <td> <pre>
MATCH
    (a: Account {address: '0x32be343b94f860124dc4fee278fdcbd38c102d88'})-[]-(t: Transaction)-[]-(b: Account)-[]-(t2: Transaction)
RETURN 
    t2
</pre> </td>
<td> <pre>
MATCH
    (a: Account {address: '0x32be343b94f860124dc4fee278fdcbd38c102d88'})-[]->(b)-[r2]-(c)
RETURN 
    r2
</pre> </td>
</tr>

<tr>
<td>Q19</td>
<td>
Conta le transazioni vicine dei primi 10 account che hanno effettuato piu transazioni
</td> <td> <pre>
MATCH 
    (n:Account)-[r:SENT]-(t:Transaction)-[:TRANSFERRED]->(b:Account)
WHERE n <> b
WITH n, COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 10
MATCH (b)-[]->(t1: Transaction)
RETURN
    COUNT(r1)
</pre> </td>
<td> <pre>
MATCH 
    (n:Account)-[r:TRANSFERRED]->(b:Account)
WHERE 
    n <> b
WITH n, COUNT(r) AS numTransactions
ORDER BY numTransactions DESC
LIMIT 10
MATCH (b)-[r2]->(c)
RETURN
    COUNT(r2)
</pre> </td>
</tr>

<tr>
<td>Q20</td>
<td>
Ritorna tutte le transazioni presenti nel blocco della transazione con hash 0x35165e50896a08024b760e400e84a82b46401d655509ad4b8b1694acd9966b3d
</td> <td> <pre>
MATCH
    (t: Transaction {hash: '0x35165e50896a08024b760e400e84a82b46401d655509ad4b8b1694acd9966b3d'})-[:CONTAINED_IN]->(b: Block)
MATCH
    (t2: Transaction)-[:CONTAINED_IN]->(b2: Block{hash: b.hash})
RETURN t2
</pre> </td>
<td> <pre>
MATCH
    (a: Account)-[t]->(b: Account)
WHERE 
    t.hash = '0x35165e50896a08024b760e400e84a82b46401d655509ad4b8b1694acd9966b3d'
MATCH
    (c: Account)-[t1 {block_hash: t.block_hash}]->(d: Account)
RETURN
    t1
</pre> </td>
</tr>

<tr>
<td>Q21</td>
<td>
Ritornare tutti gli account che hanno fatto transazioni verso l'address del DAO 0xbb9bc244d798123fde783fcc1c72d3bb8c189413
</td> <td> <pre>
MATCH 
    (a: Account)-[:SENT]->(t: Transaction)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'}) 
RETURN 
    a;
</pre> </td>
<td> <pre>
MATCH 
    (a: Account)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'}) 
RETURN 
    a;
</pre> </td>
</tr>

<tr>
<td>Q22</td>
<td>
Contare tutte le transazioni dirette verso l'indirizzo del DAO 0xbb9bc244d798123fde783fcc1c72d3bb8c189413
</td> <td> <pre>
MATCH
    (t: Transaction)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'}) 
RETURN 
    COUNT(*);
</pre> </td>
<td> <pre>
MATCH
    (a: Account)-[t]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
RETURN 
    COUNT(t);
</pre> </td>
</tr>

<tr>
<td>Q23</td>
<td>
Ritorna gli indirizzi che hanno fatto transazioni verso il DAO ordinati in modo decrescente sul numero di transazioni
</td> <td> <pre>
MATCH
    (a: Account)-[]-(t: Transaction)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
RETURN
    a.address as address,
    COUNT(*) as tot_txs, 
    SUM(t.value) as eth
ORDER BY tot_txs DESC
</pre> </td>
<td> <pre>
MATCH
    (a: Account)-[t]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
RETURN
    a.address as address,
    COUNT(*) as tot_txs, 
    SUM(t.value) as eth
ORDER BY tot_txs DESC
</pre> </td>
</tr>

<tr>
<td>Q24</td>
<td>
Ritorna gli account che hanno fatto piu transazioni verso il DAO ordinati per ether inviati
</td> <td> <pre>
MATCH
    (a: Account)-[]-(t: Transaction)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
RETURN
    a.address as address,
    COUNT(*) as tot_txs,
    SUM(t.value) as eth
ORDER BY eth DESC
</pre> </td>
<td> <pre>
MATCH
    (a: Account)-[t]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
RETURN
    a.address as address,
    COUNT(*) as tot_txs, 
    SUM(t.value) as eth
ORDER BY eth DESC
</pre> </td>
</tr>

<tr>
<td>Q25</td>
<td>
Ritorna il numero di transazioni verso il DAO raggruppate per giorno
</td> <td> <pre>
MATCH 
    (block: Block)<-[]-(txs: Transaction)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
WHERE
    block.number >= 1400000 AND block.number <=2500000
WITH datetime({epochSeconds:block.timestamp}) as datetime, count(txs) as tot_txs
RETURN
    date(datetime) as date,
    SUM(tot_txs) as day_txs
ORDER BY date ASC
</pre> </td>
<td> <pre>
MATCH
    (a: Account)-[t]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
WHERE 
    t.block_number >=1400000 AND t.block_number <= 2500000
WITH datetime({epochSeconds: t.block_timestamp}) as datetime, count(*) as tot_txs
RETURN 
    date(datetime) as date,
    sum(tot_txs) as day_txs
ORDER BY date ASC
</pre> </td>
</tr>

<tr>
<td>Q26</td>
<td>
Ritorna il numero di ether trasferiti verso il DAO raggruppati per giorno
</td> <td> <pre>
MATCH
    (block: Block)<-[]-(txs: Transaction)-[]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
WHERE
    block.number >= 1400000 AND block.number <=2000000
WITH 
    datetime({epochSeconds:block.timestamp}) as datetime, txs.value as eth
RETURN
    date(datetime) as date,
    SUM(eth) as tot_eth
ORDER BY date ASC
</pre> </td>
<td> <pre>
MATCH
    (a: Account)-[t]->(b: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
WHERE
    t.block_number >=1400000 AND t.block_number <= 2500000
WITH datetime({epochSeconds: t.block_timestamp}) as datetime, t.value as eth
RETURN
    date(datetime) as date,
    SUM(eth) as tot_eth
ORDER BY date ASC
</pre> </td>
</tr>

<tr>
<td>Q27</td>
<td>
Ritorna per i primi 100 account che hanno fatto più transazioni verso il DAO, gli indirizzi che sono più comuni (tra questi 100) ovvero gli indirizzi ai quali hanno fatto transazioni e la percentuale di quanto lo sono
</td> <td> <pre>
MATCH
    (sender: Account)-[]-(txs: Transaction)-[]->(receiver: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
WITH sender, receiver, count(*) as num_txs
ORDER BY num_txs DESC
LIMIT 100
MATCH 
    (sender: Account)-[]-(txs2: Transaction)-[]->(common_receiver: Account)
WHERE 
    common_receiver <> receiver
WITH sender, common_receiver, count(*) as tot_txs
WITH common_receiver.address as common_address, count(*) as num_occurrence
RETURN 
    common_address,
    num_occurrence,
    num_occurrence * 100 / 100 as common_percentage
ORDER BY num_occurrence DESC
</pre> </td>
<td> <pre>
match (sender: Account)-[]->(receiver: Account {address: '0xbb9bc244d798123fde783fcc1c72d3bb8c189413'})
with sender, receiver, count(*) as num_txs
order by num_txs DESC
LIMIT 100
match (sender: Account)-[]->(common_receiver: Account)
where common_receiver <> receiver
with sender, common_receiver, count(*) as tot_txs
with common_receiver.address as common_address, count(*) as num_occurrence
return common_address, num_occurrence, num_occurrence * 100 / 100 as common_percentage
order by num_occurrence DESC
</pre> </td>
</tr>

<tr>
<td>Q28</td>
<td>
Numero di transazioni totali verso EOA e SC e ether trasferiti per giorno fatte prima e dopo il DAO. Da blocco 1 200 000 a blocco 1 800 000 
</td> <td> <pre>
match (block: Block)<-[]-(txs: Transaction)-[]->(b: Account)
where block.number >= 1300000 and block.number <= 2000000
with 
    datetime({epochSeconds:block.timestamp}) as datetime,
    sum(case when b.account_type = 1 then 1 else 0 end) as tot_eoa_txs,
    sum(case when b.account_type = 2 then 1 else 0 end) as tot_sc_txs,
    sum(txs.value) as eth
return 
    date(datetime) as date,
    sum(tot_sc_txs) as tot_sc_txs,
    sum(tot_eoa_txs) as tot_eoa_txs,
    sum(eth) as tot_eth
order by date ASC
</pre> </td>
<td> <pre>
match (a: Account)-[txs]->(b: Account)
where txs.block_number >= 1300000 and txs.block_number <= 2000000
with 
    datetime({epochSeconds: txs.block_timestamp}) as datetime,
    sum(case when b.account_type = 1 then 1 else 0 end) as tot_eoa_txs,
    sum(case when b.account_type = 2 then 1 else 0 end) as tot_sc_txs,
    sum(txs.value) as eth
return 
    date(datetime) as date,
    sum(tot_sc_txs) as tot_sc_txs,
    sum(tot_eoa_txs) as tot_eoa_txs,
    sum(eth) as tot_eth
order by date asc
</pre> </td>
</tr>

<tr>
<td>Q29</td>
<td>
Numero di contratti creati nel periodo del DAO
</td> <td> <pre>
match 
    (block: Block)<-[]-(txs: Transaction)-[:CREATED]->(b: Account)
where 
    block.number >= 1200000 and block.number <=1800000
with datetime({epochSeconds:block.timestamp}) as datetime, count(*) as tot_txs
return 
    date(datetime) as date,
    sum(tot_txs) as tot_txs
order by date ASC
</pre> </td>
<td> <pre>
match 
    (a: Account)-[t:CREATED]->(b: Account)
where 
    t.block_number >=1200000 and t.block_number <= 1800000
with datetime({epochSeconds: t.block_timestamp}) as datetime, count(*) as tot_txs
return
    date(datetime) as date, sum(tot_txs)
order by date asc
</pre> </td>
</tr>

</tbody>
</table>



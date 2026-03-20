// Projected graph test
MATCH (from: Account)-[:sent]-(t:Transaction)-[:transfered]->(to: Account)
return from,to, apoc.create.vRelationship(from, 'transfered',{amount: t.value}, to) limit 100

// PROJECTED GRAPH BUILD

// Projected graph transfer
MATCH (from: Account)-[:sent]-(t:Transaction)-[:transfered]->(to: Account)
WITH 
    gds.graph.project('transfer',from, to) as g
RETURN  
    g.graphName AS graph,
    g.nodeCount AS nodes,
    g.relationshipCount AS rels

// Projected graph invocation
MATCH (from: Account)-[:sent]-(t:Transaction)-[:invoked]->(to: Account)
with gds.graph.project('invocation',from, to) as g
return  g.graphName AS graph, g.nodeCount AS nodes, g.relationshipCount AS rels

// Projected graph contract creation
MATCH (from: Account)-[:sent]-(t:Transaction)-[:created]->(to: Account)
with gds.graph.project('sc_creation',from, to) as g
return  g.graphName AS graph, g.nodeCount AS nodes, g.relationshipCount AS rels

// Projected graph for all account interaction
MATCH (from: Account)-[:sent]-(t:Transaction)-[]->(to: Account)
with gds.graph.project('all_interaction',from, to) as g
return  g.graphName AS graph, g.nodeCount AS nodes, g.relationshipCount AS rels

// PROJECTED GRAPH QUERY

// Page rank
call gds.pageRank.stream('transfer') yield nodeId, score
return gds.util.asNode(nodeId).address as address, score
order by score DESC

// Node centrality
call gds.degree.stream('transfer') yield nodeId, score
return gds.util.asNode(nodeId).address as address, score
order by score DESC
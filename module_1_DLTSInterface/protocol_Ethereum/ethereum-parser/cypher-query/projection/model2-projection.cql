// Projeted graph
match (from: Account)-[:transfered]->(to: Account)
with gds.graph.project('transfer', from, to) as g
return g.graphName as graph, g.nodeCount as nodes, g.relationshipCount as rels

match (from: Account)-[:invoked]->(to: Account)
with gds.graph.project('invocation', from, to) as g
return g.graphName as graph, g.nodeCount as nodes, g.relationshipCount as rels

match (from: Account)-[:created]->(to: Account)
with gds.graph.project('sc_creation', from, to) as g
return g.graphName as graph, g.nodeCount as nodes, g.relationshipCount as rels

match (from: Account)-[]->(to: Account)
with gds.graph.project('all_interaction', from, to) as g
return g.graphName as graph, g.nodeCount as nodes, g.relationshipCount as rels
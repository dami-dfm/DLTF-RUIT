// API logic here

// THIS IS A MOCKUP, USED TO TEST THE FRONT-END. 
// METHODS RETURN FICTITIOUS VALUES, NOSENSE NUMBERS
// DO NOT USE IT IN PRODUCTION

export interface Pair {
    sample: string,
    value: number
}

export interface Node {
    id: number,
    address: string,
    indegree: number,
    outdegree: number,
    scale: number,
    timestamp: string,
    timestamp_view: string, // fictitious timestamp randomly generated to test the time filters in absence of the real one
}

export interface Link {
    fromId: number,
    toId: number,
    amount: number,
    scale: number,
    timestamp: string,
    timestamp_view: string // fictitious timestamp randomly generated to test the time filters in absence of the real one
}

async function getSubgraphData(nodeId: any): Promise<any> {
    const apiUrl = process.env.GRAPH_API_BASE_URL + nodeId; 
    const response = await fetch(apiUrl);
    return await response.json();
}

// this function calls the data retrieval level and returns the filtered graph data in a suitable format
// TODO: add filters based on arbitrary dates besides fixed time intervals
export async function getSubgraph(address: any, timeInterval: any, nodeId: number): Promise<any> {
    let subgraph: any = {};
    let apiUrl = process.env.GRAPH_API_BASE_URL || "http://localhost:3000";
    const nodes = [];
    const inLinks: Link[] = [];
    const outLinks: Link[] = [];

    const url = apiUrl + nodeId;
    const data = await getSubgraphData(nodeId);

    let inAmount: number = 0;
    let outAmount: number = 0;

    let minTxValue: number = 1000000000;
    let maxTxValue: number = 0;
    let minOutdegree: number = 1000000000;
    let maxOutdegree: number = 0;

    for (const link of data.inlinks) {
        const l: Link = {
            fromId: link.source_id,
            toId: link.target_id,
            amount: link.amount,
            scale: 0,
            timestamp: link.timestamp,
            timestamp_view: getRandomTimestamp()
        }
       
        if (isWithinInterval(l.timestamp_view, timeInterval)) {
            inAmount += l.amount;
            inLinks.push(l);
        }
    }

    for (const link of data.outlinks) {
        const l: Link = {
            fromId: link.source_id,
            toId: link.target_id,
            amount: link.amount,
            scale: 0,
            timestamp: link.timestamp,
            timestamp_view: getRandomTimestamp()
        }
        if (isWithinInterval(l.timestamp_view, timeInterval)) {
            if (l.amount < minTxValue) {
                minTxValue = l.amount;
            }
            if (l.amount > maxTxValue) {
                maxTxValue = l.amount;
            }
            outAmount += l.amount;
            outLinks.push(l);
        }
    }

    for (const entry of data.nodes) {
        const n: Node = {
            id: entry.id,
            address: entry.address,
            indegree: entry.indegree,
            outdegree: entry.outdegree,
            scale: 0,
            timestamp: entry.creation_timestamp,
            timestamp_view: getRandomTimestamp()
        }
        if (isWithinInterval(n.timestamp_view, timeInterval)) {
            if (entry.outdegree < minOutdegree) {
                minOutdegree = entry.outdegree;
            }
            if (entry.outdegree > maxOutdegree) {
                maxOutdegree = entry.outdegree;
            }
            nodes.push(n);
        }
    }

    for (const node of nodes) {
        node.scale = getNormalization(node.outdegree, minOutdegree, maxOutdegree);
    }

    for (const link of outLinks) {
        link.scale = getNormalization(link.amount, minTxValue, maxTxValue);
    } 

    subgraph.currentNode = getNodeData(nodeId);
    subgraph.currentNode.indegree = inLinks.length;
    subgraph.currentNode.outdegree = outLinks.length;
    subgraph.nodes = nodes;
    subgraph.in_amount = inAmount;
    subgraph.out_amount = outAmount;
    subgraph.inlinks = inLinks;
    subgraph.outlinks = outLinks;

    return subgraph;
}

// this function re-constructs the path of the incoming funds for a given node within the selected numebr of steps
export async function getNodeTxPath(address: any, steps: number): Promise<any> {
    let path: any = [];
    let s: number = 0;
    // PLACEHOLDER
    let nodeId = 37028167;

    while (s < steps) {
        let subgraph = await getSubgraph(address, '1y', nodeId);
        nodeId = subgraph.inLinks.fromId;
        address = subgraph.currentNode.address;

        path.push(nodeId);
        s++;
    }

    return path;
}

// this function returns the selected node ID. 
// placeholder until a clear ID-address mapping is designed
// not needed if full graph node data are present
function getNodeIdByAddress(address: string, placeholder = -1): number {
    if (address = '')
        return 37028167;
    else if (placeholder != -1) {
        return placeholder;
    } else if (placeholder == -1){
        return 37028167;
    } else {
        return -1;
    }
}

// this functions normalizes values within a specified interval
// udes to calculate the transparency of nodes and edges in trhe webapp
function getNormalization(value: number, min: number, max: number): number {
    let normalizedValue = 0.5;
    if (min != max) {
        normalizedValue = (value - min)/(max - min);
    }
    return normalizedValue;
}

// tis function returns a selected node data.
// placeholder ID selected and placeholder data structure returned
export function getNodeData(id: number): Node {
    const node = {
        id: 37028167,
        address: "0x0",
        indegree: 1,
        outdegree: 0, // 
        in_amount: 0,
        out_amount: 0,
        scale: 0.5,
        timestamp: "",
        timestamp_view: Date.now().toString()
    };
    return node;
}

// function used to produce random timestamps for nodes/edges, in order to test temporal filters
// not needeed when full graph data will be available
function getRandomTimestamp(): string {
    const now = Date.now();
    const oneYearsAgo = now - 365 * 24 * 60 * 60 * 1000;

    const randomTime =
        oneYearsAgo + Math.random() * (now - oneYearsAgo);

    return new Date(randomTime).toISOString(); // string
}

function isWithinInterval(timestampStr: string, interval: string) {
    const timestamp = new Date(timestampStr).getTime();
    if (isNaN(timestamp)) {
        throw new Error("Invalid timestamp string");
    }

    const now = Date.now();
    let intervalMs = -1;

    switch (interval) {
        case '1d':
            intervalMs = 24 * 60 * 60 * 1000;
            break;
        case '1w':
            intervalMs = 7 * 24 * 60 * 60 * 1000;
            break;
        case '1m':
            intervalMs = 30 * 24 * 60 * 60 * 1000;
            break;
        case '1y':
            intervalMs = 365 * 24 * 60 * 60 * 1000;
            break;
        default:
            intervalMs = -1;
            break;
    }

    if (intervalMs == -1) {
        throw new Error("Invalid interval. Use 1d, 1w, 1m, or 1y.");
    }

    return timestamp >= now - intervalMs && timestamp <= now;
}

// average transactions in the time period for the selected smart contract
export function scOverallAvg(address: any, samplingInterval: any): any {
    return {overallAvg: 82}
}

// number of invocation received by the selected smart contract in the time period
// placeholder data considering an yearly time interval
export function scLiveness(address: any, timeInterval: any, samplingInterval: any): Pair[] {
    let sequencePair: Pair[] = [];
    sequencePair.push({ sample: 'January', value: 10 });
    sequencePair.push({ sample: 'February', value: 17 });
    sequencePair.push({ sample: 'March', value: 9 });
    sequencePair.push({ sample: 'April', value: 13 });
    sequencePair.push({ sample: 'May', value: 11 });
    sequencePair.push({ sample: 'June', value: 20 });
    sequencePair.push({ sample: 'July', value: 19 });
    sequencePair.push({ sample: 'August', value: 27 });
    sequencePair.push({ sample: 'September', value: 26 });
    sequencePair.push({ sample: 'October', value: 22 });
    sequencePair.push({ sample: 'November', value: 19 });
    sequencePair.push({ sample: 'December', value: 14 });
    return sequencePair;
}

// number of unique accounts invoking the selected smart contract address
// placeholder data considering a weekly time interval
export function scPopularity(address: any, timeInterval: any, samplingInterval: any): Pair[] {
    let sequencePair: Pair[] = [];
    sequencePair.push({ sample: 'Monday', value: 8 });
    sequencePair.push({ sample: 'Tuesday', value: 14 });
    sequencePair.push({ sample: 'Wednesday', value: 15 });
    sequencePair.push({ sample: 'Thursday', value: 9 });
    sequencePair.push({ sample: 'Friday', value: 22 });
    sequencePair.push({ sample: 'Saturday', value: 27 });
    sequencePair.push({ sample: 'Sunday', value: 19 });
    return sequencePair;
}

// average transactions in the time period for the selected EOA
export function eoaOverallAvg(address: any, samplingInterval: any, type: any): any {
    return {overallAvg: 129};
}

// number of invocation received by the selected EOA in the time period
// placeholder data considering an yearly time interval
export function eoaLiveness(address: any, timeInterval: any, samplingInterval: any, type: any): Pair[] {
    let sequencePair: Pair[] = [];
    sequencePair.push({ sample: 'January', value: 20 });
    sequencePair.push({ sample: 'February', value: 44 });
    sequencePair.push({ sample: 'March', value: 60 });
    sequencePair.push({ sample: 'April', value: 54 });
    sequencePair.push({ sample: 'May', value: 75 });
    sequencePair.push({ sample: 'June', value: 58 });
    sequencePair.push({ sample: 'July', value: 44 });
    sequencePair.push({ sample: 'August', value: 51 });
    sequencePair.push({ sample: 'September', value: 60 });
    sequencePair.push({ sample: 'October', value: 66 });
    sequencePair.push({ sample: 'November', value: 59 });
    sequencePair.push({ sample: 'December', value: 55 });
    return sequencePair;
}

// number of unique accounts invoking the selected EOA address
// placeholder data considering a daily time interval
export function eoaPopularity(address: any, timeInterval: any, samplingInterval: any): Pair[] {
    let sequencePair: Pair[] = [];
    sequencePair.push({ sample: '00:00', value: 2 });
    sequencePair.push({ sample: '01:00', value: 1 });
    sequencePair.push({ sample: '02:00', value: 2 });
    sequencePair.push({ sample: '03:00', value: 0 });
    sequencePair.push({ sample: '04:00', value: 1 });
    sequencePair.push({ sample: '05:00', value: 1 });
    sequencePair.push({ sample: '06:00', value: 0 });
    sequencePair.push({ sample: '07:00', value: 2 });
    sequencePair.push({ sample: '08:00', value: 2 });
    sequencePair.push({ sample: '09:00', value: 3 });
    sequencePair.push({ sample: '10:00', value: 5 });
    sequencePair.push({ sample: '11:00', value: 5 });
    sequencePair.push({ sample: '12:00', value: 10 });
    sequencePair.push({ sample: '13:00', value: 8 });
    sequencePair.push({ sample: '14:00', value: 13 });
    sequencePair.push({ sample: '15:00', value: 12 });
    sequencePair.push({ sample: '16:00', value: 17 });
    sequencePair.push({ sample: '17:00', value: 20 });
    sequencePair.push({ sample: '18:00', value: 21 });
    sequencePair.push({ sample: '19:00', value: 17 });
    sequencePair.push({ sample: '20:00', value: 18 });
    sequencePair.push({ sample: '21:00', value: 10 });
    sequencePair.push({ sample: '22:00', value: 8 });
    sequencePair.push({ sample: '23:00', value: 2 });
    return sequencePair;
}

// number of unique accounts that the selected EOA sent funds to
// placeholder data considering a weekly time interval
export function eoaDiversification(address: any, timeInterval: any, samplingInterval: any, recvType: any): Pair[] {
    let sequencePair: Pair[] = [];
    sequencePair.push({ sample: 'Monday', value: 8 });
    sequencePair.push({ sample: 'Tuesday', value: 14 });
    sequencePair.push({ sample: 'Wednesday', value: 15 });
    sequencePair.push({ sample: 'Thursday', value: 9 });
    sequencePair.push({ sample: 'Friday', value: 22 });
    sequencePair.push({ sample: 'Saturday', value: 27 });
    sequencePair.push({ sample: 'Sunday', value: 19 });
    return sequencePair;
}

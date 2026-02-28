import { Router } from "express";

import * as logic from "../logic";

const router = Router();

router.get('/subgraph', async (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let nodeId = req.query.nodeId;

    let id = 37028167;
    if (nodeId != undefined) {
        id = Number(nodeId);
    }
    res.json(await logic.getSubgraph(address, timeInterval, id));
});

router.get('/kcore', async (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let nodeId = req.query.nodeId;

    let id = 37028167;
    if (nodeId != undefined) {
        id = Number(nodeId);
    }
    res.json(await logic.getKCoreData(address, timeInterval, id));
});

router.get('/sc-overall-avg', (req, res) => {
    let address = req.query.address;
    let sampleInterval = req.query.sample;
    res.json(logic.scOverallAvg(address, sampleInterval));
});

router.get('/sc-liveness', (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let sampleInterval = req.query.sample;
    res.json(logic.scLiveness(address, timeInterval, sampleInterval));
});

router.get('/sc-popularity', (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let sampleInterval = req.query.sample;
    res.json(logic.scPopularity(address, timeInterval, sampleInterval));
});

router.get('/eoa-overall-avg', (req, res) => {
    let address = req.query.address;
    let sampleInterval = req.query.sample;
    let type = req.query.type;
    res.send(logic.eoaOverallAvg(address, sampleInterval, type));
});

router.get('/eoa-liveness', (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let sampleInterval = req.query.sample;
    let type = req.query.type;
    res.json(logic.eoaLiveness(address, timeInterval, sampleInterval, type));
});

router.get('/eoa-popularity', (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let sampleInterval = req.query.sample;
    res.json(logic.eoaPopularity(address, timeInterval, sampleInterval));
});

router.get('/eoa-diversification', (req, res) => {
    let address = req.query.address;
    let timeInterval = req.query.timeInterval;
    let sampleInterval = req.query.sample;
    let recvType = req.query.recvType;
    res.json(logic.eoaDiversification(address, timeInterval, sampleInterval, recvType));
});

export default router;

import express from "express"
import * as dotenv from "dotenv"
import cors from "cors"
import helmet from "helmet"

import * as dreRoutes from "./routes/dre"

dotenv.config()

if (!process.env.PORT) {
    console.log("No port specified")
}

const PORT = parseInt(process.env.PORT as string, 10) || 3001;

const app = express();

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(cors());
app.use(helmet());

app.use('/dre', dreRoutes.default);

app.get('/hello', (req, res) => {
    res.status(200).send("ok");
});

app.listen(PORT, () => {
    console.log("App listening on port " + PORT)
});

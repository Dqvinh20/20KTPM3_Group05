const router = require("express").Router();
const SearchService = require("../services/search.service");

const LIMIT = 5;
const MAX_LIMIT = 15;

router.get("/", async (req, res) => {
    try {
        let { query, limit } = req.query;
        if (!query) return res.json({ error: "No query" });
        if (!limit) limit = LIMIT;
        else if (limit > MAX_LIMIT) limit = MAX_LIMIT;

        const result = {
            users: await SearchService.searchUser(query, { limit }),
        };

        return res.json(result);
    } catch (error) {
        return res.json({ error: error.message });
    }
});

router.get("/location", async (req, res) => {
    try {
        let { query, limit } = req.query;
        if (!query) return res.json({ error: "No query" });
        if (!limit) limit = LIMIT;
        else if (limit > MAX_LIMIT) limit = MAX_LIMIT;

        const result = {
            locations: await SearchService.searchLocation(query, { limit }),
        };

        return res.json(result);
    } catch (error) {
        return res.json({ error: error.message });
    }
});

module.exports = router;

const router = require("express").Router();
const SearchService = require("../services/search.service");

router.get("/", async (req, res) => {
  const { query } = req.query;
  const result = {
    posts: await SearchService.searchPost(query),
    users: await SearchService.searchUser(query),
  };
  res.json(result);
});

module.exports = router;

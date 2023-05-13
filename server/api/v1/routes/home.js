const router = require("express").Router();
const HomeController = require("../controllers/home.controller");

router.get("/popular", HomeController.getPopularPosts);

router.get("/newest", HomeController.getNewsFeed);

module.exports = router;

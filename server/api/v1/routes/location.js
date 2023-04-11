const router = require("express").Router();
const LocationController = require("../controllers/location.controller");

router.get("/", LocationController.getAllLocations);

module.exports = router;

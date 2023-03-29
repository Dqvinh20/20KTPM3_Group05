const express = require("express");
const router = express.Router();
const Validation = require("../utils/validation");
const RatingController = require("../controllers/rating.controller");

const validateData = [];

router.post(
    "/create",
    validateData,
    Validation.validate,
    RatingController.createRating
);

module.exports = router;

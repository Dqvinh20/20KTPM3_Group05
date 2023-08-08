const { validationResult } = require("express-validator");
const passport = require("passport");
const ResponseType = require("../models/response.model");

const validateData = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.json(ResponseType.Error(errors.array()));
  }
  next();
};

module.exports = {
  validate: validateData,
};

const { ValidationError } = require("sequelize");
const jwt = require("jsonwebtoken");

const UserService = require("../services/user.service");
const ResponseType = require("../models/response.model");

const login = async (req, res) => {
  const { email, password } = req.body;

  try {
    const user = await UserService.getUserByEmail(email);
    if (!user) {
      return res.json(ResponseType.Failure("E-mail or password incorrect"));
    }
    if (!user.checkPassword(password, user.password)) {
      return res.json(ResponseType.Failure("E-mail or password incorrect"));
    }

    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET,
      {
        expiresIn: process.env.JWT_EXPIRES_IN,
      }
    );

    return res.json(ResponseType.Success({ token }));
  } catch (error) {
    if (error instanceof ValidationError) {
      return res.json(
        ResponseType.Error({
          name: error.name,
          msg: error.errors[0].message,
        })
      );
    }
    return res.json(ResponseType.Error(error.message));
  }
};

const signup = async (req, res) => {
  const { email, password } = req.body;
  try {
    await UserService.createUser({ email, password });
    return res.status(201).json(ResponseType.Success("User created"));
  } catch (error) {
    if (error instanceof ValidationError) {
      return res.json(
        ResponseType.Error({
          name: error.name,
          msg: error.errors[0].message,
        })
      );
    }
    return res.json(ResponseType.Error(error.message));
  }
};

module.exports = {
  login,
  signup,
};

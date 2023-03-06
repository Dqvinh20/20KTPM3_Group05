"use strict";
const { Op } = require("sequelize");
const User = require("../../api/v1/models/user.model");

/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up(queryInterface, Sequelize) {
    var users = [];
    for (let i = 0; i < 15; i++) {
      const user = User.build({
        email: `vinh${i}@gmail.com`,
        password: "123456",
        createdAt: new Date(),
        updatedAt: new Date(),
      }).toJSON();
      delete user.id;
      users.push(user);
    }

    await queryInterface.bulkInsert("users", users, {});
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.bulkDelete(
      "users",
      {
        where: {
          id: {
            [Op.gt]: 4,
          },
        },
      },
      {}
    );
  },
};

"use strict";

/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up(queryInterface, Sequelize) {
    return Promise.all([
      queryInterface.addColumn("users", "username", {
        type: Sequelize.STRING,
      }),
      queryInterface.addColumn("users", "active", {
        type: Sequelize.BOOLEAN,
        defaultValue: false,
      }),
    ]);
  },

  async down(queryInterface, Sequelize) {
    return Promise.all([
      queryInterface.removeColumn("users", "username"),
      queryInterface.removeColumn("users", "active"),
    ]);
  },
};

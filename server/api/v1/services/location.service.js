const Location = require("../models/location.model");

const hardReset = async () => {
  return await Location.sync({ force: true });
};

const getLocationById = async (location_id) => {
  return await Location.findByPk(location_id);
};

const createLocation = async (location) => {
  const newLocation = await Location.create(location);
  return newLocation;
};

const updateLocation = async (location_id, data) => {
  return await Location.update(data, { where: { id: location_id } });
};

const deleteLocation = async (location_id) => {
  return await Location.destroy({ where: { id: location_id } });
};

module.exports = {
  getLocationById,
  createLocation,
  updateLocation,
  deleteLocation,
  hardReset,
};

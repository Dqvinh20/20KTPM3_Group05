const Location = require("../models/location.model");

const getAllLocations = async () => {
  return await Location.findAll();
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
  getAllLocations,
  getLocationById,
  createLocation,
  updateLocation,
  deleteLocation,
};

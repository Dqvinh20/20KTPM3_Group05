const LocationService = require("../services/location.service");

const getAllLocations = async (req, res) => {
  try {
    const result = await LocationService.getAllLocations();
    return res.json(result);
  } catch (error) {
    return res.status(400).json({ error: error.message });
  }
};

module.exports = {
  getAllLocations,
};

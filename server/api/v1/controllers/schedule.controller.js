const ScheduleService = require("../services/schedule.service");

const getAll = async (req, res) => {
  const result = await ScheduleService.model.findAll({
    include: [
      {
        association: "locations",
        attributes: { exclude: ["place_id", "_search"] },
      },
    ],
  });
  return res.json(result);
};

const getScheduleById = async (req, res) => {
  const result = await ScheduleService.model.findAll({
    include: [
      {
        association: "locations",
        attributes: { exclude: ["place_id", "_search"] },
      },
    ],
    where: { id: req.params.id },
  });
  return res.json(result);
};

const addLocation = async (req, res) => {
  try {
    let { schedule_id, location_id } = req.body;
    location_id = Number(location_id);

    const newLocation = await ScheduleService.addLocation(
      schedule_id,
      location_id
    );

    return res.json(newLocation[0]);
  } catch (error) {
    return res.status(400).json({ error: error.message });
  }
};

const removeLocation = async (req, res) => {
  try {
    let { schedule_id, location_pos } = req.body;
    location_pos = Number(location_pos);

    const isSuccess = await ScheduleService.removeLocation(
      schedule_id,
      location_pos
    );

    return res.json(isSuccess);
  } catch (error) {
    return res.status(400).json({ error: error.message });
  }
};

const editLocationNote = async (req, res) => {
  try {
    let { schedule_id, location_pos, note } = req.body;
    location_pos = Number(location_pos);

    const result = await ScheduleService.editLocationNote(
      schedule_id,
      location_pos,
      note
    );

    return res.json(result);
  } catch (error) {
    return res.status(400).json({ error: error.message });
  }
};

module.exports = {
  getAll,
  getScheduleById,
  addLocation,
  removeLocation,
  editLocationNote,
};

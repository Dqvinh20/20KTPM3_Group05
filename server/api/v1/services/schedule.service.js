const { Op, json } = require("sequelize");
const dayjs = require("dayjs");

const Schedule = require("../models/schedule.model");
const Post = require("../models/post.model");
const SchedulesLocations = require("../models/schedule_location.model");
const Converter = require("../utils/converter");
const LocationService = require("../services/location.service");

const commonSchedule = (opts) => {
  return {
    attributes: {
      exclude: ["post_id"],
    },
    include: [
      {
        association: "locations",
        attributes: {
          exclude: ["place_id", "_search"],
        },
      },
    ],
    ...opts,
  };
};

const getAllLocationsInSchedule = async (schedule_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  return await schedule.getLocations();
};

const addLocation = async (schedule_id, location_id) => {
  const schedule = await Schedule.findByPk(schedule_id);
  if (!schedule) {
    throw new Error("Schedule does not exist");
  }

  const location = await LocationService.getLocationById(location_id);
  if (!location) {
    throw new Error("Location does not exist");
  }

  const [locationResult, scheduleResult] = await Promise.all([
    await SchedulesLocations.create({
      schedule_id: schedule_id,
      location_id: location_id,
    }),
    schedule.increment("location_count", { by: 1 }),
  ]);

  locationResult.position = scheduleResult.location_count;
  await locationResult.save();

  let result = await schedule.getLocations({
    where: { id: location_id },
    through: { where: { id: locationResult.id } },
  });

  return result;
};

const editLocationNote = async (schedule_id, location_pos, note) => {
  const locationInSchedule = await SchedulesLocations.findOne({
    where: {
      schedule_id: schedule_id,
      position: location_pos,
    },
  });

  if (!locationInSchedule) {
    throw new Error("Note does not exist");
  }

  locationInSchedule.note = note;
  await locationInSchedule.save();
  return locationInSchedule;
};

const removeLocation = async (schedule_id, location_pos) => {
  const schedule = await Schedule.findByPk(schedule_id);
  if (!schedule) {
    throw new Error("Schedule does not exist");
  }

  let currPosition = await schedule.getLocations({
    through: { attributes: ["position"], where: { position: location_pos } },
  });

  if (!currPosition) {
    throw new Error("Location does not exist in schedule");
  }

  if (currPosition.length !== 0) {
    currPosition = currPosition[0].SchedulesLocations.position;
  }

  const result = await SchedulesLocations.destroy({
    where: {
      schedule_id: schedule_id,
      position: location_pos,
    },
  });
  await schedule.decrement("location_count", { by: result });

  if (result === 1) {
    const nextLocations = await schedule.getLocations({
      through: {
        where: {
          position: {
            [Op.gt]: currPosition,
          },
        },
      },
    });

    await Promise.all(
      nextLocations.map(async (location) => {
        location.SchedulesLocations.position -= 1;
        await location.SchedulesLocations.save();
      })
    );
  }

  return result;
};

const getAllSchedulesInPost = async (post_id) => {
  const result = await Schedule.findAll(
    commonSchedule({
      where: {
        post_id: post_id,
      },
    })
  );
  return result;
};

const getScheduleById = async (id) => {
  return await Schedule.findByPk(id);
};

const removeAllSchedules = async (post_id) => {
  return await Schedule.destroy({ where: { post_id: post_id } });
};

const changeScheduleRange = async (post_id, start, end) => {
  const post = await Post.findByPk(post_id, {
    attributes: ["start_date", "end_date"],
  });
  const newStart = new Date(start);
  const newEnd = new Date(end);
  const oldStart = new Date(post.start_date);
  const oldEnd = new Date(post.end_date);

  if (
    newStart.getTime() === oldStart.getTime() &&
    newEnd.getTime() === oldEnd.getTime()
  ) {
    return 0;
  }

  if (newStart > newEnd) {
    throw new Error("Start date must be before end date");
  }

  await removeBetween(
    post_id,
    post.start_date,
    dayjs(newStart).subtract(1, "day").toDate()
  );

  await removeBetween(
    post_id,
    dayjs(newEnd).add(1, "day").toDate(),
    post.end_date
  );
  await addBetween(post_id, newStart, newEnd);

  const result = await Post.update(
    {
      start_date: newStart,
      end_date: newEnd,
    },
    {
      where: {
        id: post_id,
      },
    }
  );
  return result[0];
};

const addBetween = async (post_id, start, end) => {
  const schedules = [];
  const start_date = new Date(start);
  const end_date = new Date(end);

  const diffDate = end_date.getDate() - start_date.getDate();
  let current_date = new Date(start_date);

  for (let i = 0; i <= diffDate; i++) {
    const datePlan = new Date(current_date);
    await Schedule.findOne({
      where: {
        post_id,
        date: datePlan,
      },
    }).then((result) => {
      if (!result) {
        schedules.push({
          post_id: post_id,
          date: datePlan,
          title: Converter.toScheduleTitle(datePlan),
        });
      }
    });
    current_date.setDate(current_date.getDate() + 1);
  }

  return await Schedule.bulkCreate(schedules);
};

const removeBetween = async (post_id, start, end) => {
  return await Schedule.destroy({
    where: {
      [Op.and]: [
        { post_id },
        {
          date: {
            [Op.between]: [new Date(start), new Date(end)],
          },
        },
      ],
    },
  });
};

module.exports = {
  getAllLocationsInSchedule,
  editLocationNote,
  addLocation,
  removeLocation,
  getAllSchedulesInPost,
  getScheduleById,
  removeAllSchedules,
  changeScheduleRange,
  model: Schedule,
};

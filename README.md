# Activity Tracker

## Brief Overview

This project is a distributed app developed for the course of Distributed Systems (INF507) at AUEB, over the course of the 2022-23 spring semester.

The project utilises a simplified version of the MapReduce programming model, with one master node and multiple worker nodes. The workers are used as both map and reduce workers, to simplify the process.
The user sends gpx files to the master node, which are then mapped to workers for calculations.

The system consists of a frontend mobile application for the UI (which is contained in this project), along with a [backend](https://github.com/kwstaseL/Activity-Tracker-Backend) system for the data analysis.

## Frontend System

The frontend system contains a simple mobile application, that serves as the user interface for the system.

It enables users to add activities in the form of gpx files, as well as track their progress in a user-friendly manner.

## Features

- Personal statistics for a user, which include (but are not limited to) the number of activities recorded, total distance, and total exercise time.

- Graphical and visual comparisons of the user's statistics with other registered users.

- Segment tracking: The app supports the registration of segments, akin to [Strava](https://www.strava.com/). In the backend system, there is a "registered_segments" directory of gpx files which correspond to segments, which the master registers on initiation of the app. When a user sends a gpx file which contains the specific list of waypoints one or more segments contain, the time it took the user to complete the segment is calculated and their results are placed in a leaderboard for the respective segment. A user can then view all the leaderboards for the segments they have registered.

## Usage

1. Set up the backend. You can find the setup instructions on the [backend repository](https://github.com/kwstaseL/Activity-Tracker-Backend).
2. Clone the project.
3. Open Android Studio.
4. Setup the SDK path accordingly in the local.properties file.
5. Configure the configuration file:

    • Locate the "config.properties" file, which can be found in the assets folder.
    • Update the "master_IP" and "master_port" attributes accordingly, to reflect the IP and port you are using on the pc that will be used as the master.

6. If you would like to send a file (and not just view existing results), move your GPX files to the Downloads folder of your emulator.
7. Build and run the frontend application.

## Collaborators

- [hvlkk](https://www.github.com/hvlkk)
- [kwstaseL](https://www.github.com/kwstaseL)

## License

This project is licensed under the [MIT](https://choosealicense.com/licenses/mit/) License.

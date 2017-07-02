# rpi-robot
Raspberry-Pi

This is a project for dojocon 2017 (http://coderdojowa.org.au/dojocon/), 2 ninjas are working on this, Michelle and Archie are writing the software and Graham is mentoring. We are making a raspberry pi robot that uses machine learning and the google cloud API to move around and take pictures of objects and then uses text to speech software to describe the object. To make the robot we are going to use the GoPiGo base model and build ontop of that with a raspberry pi. We are using Python `2.7.9`.

Tech stack:

- Python 2.7.9
- Raspbery-Pi (model 2B)
- Docker
- Google Cloud Vision API
- Git
- python-espeack

There are some Python modules needed. Simply run this: `pip install -r requirements.txt`

Before doing anything run these commands on the terminal:

- `sudo apt-get update`
- `sudo apt-get install python-dev`
- `sudo pip install --upgrade pip`
- `sudo apt-get install python-espeak`

https://cloud.google.com/vision/docs/reference/libraries#client-libraries-install-python

Archie, Michelle and Graham

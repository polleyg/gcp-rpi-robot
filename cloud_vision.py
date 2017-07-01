import io
import os

from google.cloud import vision

from picamera import PiCamera
from time import sleep

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "cloud_vision_key.json"

camera = PiCamera()

vision_client = vision.Client()

camera.rotation = 180
camera.start_preview()
sleep(5)
camera.capture("image_test.jpg")
camera.stop_preview()

file_name = os.path.join(os.path.dirname(__file__), 'image_test.jpg')

with io.open(file_name, 'rb') as image_file:
	content = image_file.read()
	image = vision_client.image(content=content)

labels = image.detect_labels()

print('Labels:')
for label in labels:
	print(label.description) 

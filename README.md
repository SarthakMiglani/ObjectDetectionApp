# Object Detection Android App

This is a simple Android application that performs **on-device object detection** using **TensorFlow Lite** and the device camera.

## Features
- Capture images using the device camera (CameraX)
- Run real-time object detection using a TFLite SSD MobileNet model
- Draw bounding boxes and labels on detected objects
- Display the processed image on screen
- Maintain a history of detections using Room database (stores image path + detected labels)

## Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **CameraX**
- **TensorFlow Lite**
- **Room Database**
- **MVVM Architecture**
- **Kotlin Coroutines**

## Architecture Overview
- **UI Layer:** Jetpack Compose (`DetectionScreen`)
- **ViewModel:** Handles camera actions, inference, and state management
- **ML Layer:** `ObjectDetectionService` performs TensorFlow Lite inference
- **Data Layer:** Room database stores detection history

## Notes
- All ML inference runs fully **on-device** (no network usage).
- The model is initialized once and reused for performance.
- Image files are stored locally, while metadata is stored in Room.
- Detection accuracy depends on the pretrained model.

## How to Run
1. Open the project in Android Studio
2. Place `model.tflite` and `labels.txt` inside the `assets` folder
3. Grant camera permission when prompted
4. Build and run on a physical Android device


import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: const MapScreen(),
    );
  }
}

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  // location
  final LatLng _usmLocation = const LatLng(5.3567, 100.2965);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Banana Recycle Map'),
        backgroundColor: Colors.green,
      ),
      body: GoogleMap(
        initialCameraPosition: CameraPosition(
          target: _usmLocation, // camera
          zoom: 15.0, // zoom level
        ),
        markers: {
          // 🚩 marker
          Marker(
            markerId: const MarkerId('usm_bin'),
            position: _usmLocation,
            infoWindow: const InfoWindow(title: 'Recycling Bin Here!'),
          ),
        },
      ),
    );
  }
}
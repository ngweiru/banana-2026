import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:geolocator/geolocator.dart';
import 'package:url_launcher/url_launcher.dart';
import 'dart:math';
import 'dart:ui' as ui;
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      locale: const Locale('en', 'US'),
      themeMode: ThemeMode.system,
      theme: ThemeData.light(),
      darkTheme: ThemeData.dark(),
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
  GoogleMapController? _mapController;
  BitmapDescriptor? customIcon;

  List<Map<String, dynamic>> _allStations = [];
  List<Map<String, dynamic>> _recyclingStations = [];

  String _selectedFilter = 'All';
  final List<String> _filterOptions = [
    'All',
    '24 Hours',
    '🟢 Low Traffic',
    '< 10km',
  ];

  // Default camera position (USM Penang)
  final LatLng _usmLocation = const LatLng(5.3567, 100.2965);

  // 🌑 Google Maps Dark Mode Style
  final String _darkMapStyle = '''
[
  {
    "elementType": "geometry",
    "stylers": [{"color": "#212121"}]
  },
  {
    "elementType": "labels.icon",
    "stylers": [{"visibility": "off"}]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#757575"}]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [{"color": "#212121"}]
  },
  {
    "featureType": "administrative",
    "elementType": "geometry",
    "stylers": [{"color": "#757575"}]
  },
  {
    "featureType": "administrative.country",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#9e9e9e"}]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#757575"}]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [{"color": "#181818"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry.fill",
    "stylers": [{"color": "#2c2c2c"}]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#8a8a8a"}]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{"color": "#000000"}]
  }
]
''';

  @override
  void initState() {
    super.initState();
    _loadCustomMarker();
    _generateMassiveData();
    _requestLocationPermission();

    Future.delayed(const Duration(seconds: 1), () {
      _sortStationsByDistance();
    });
  }

  void _generateMassiveData() {
    final Random random = Random();
    int idCounter = 1;

    // 🇲🇾 13 + 3
    final Map<String, List<LatLng>> stateData = {
      "Johor": [
        LatLng(2.0325, 103.3230),
        LatLng(1.4927, 103.7414),
        LatLng(2.5065, 102.8156),
        LatLng(1.8494, 102.9288),
        LatLng(2.0469, 102.5694),
        LatLng(1.5000, 103.6000),
        LatLng(1.4600, 103.7500),
        LatLng(2.4300, 103.8300),
        LatLng(1.7000, 103.4000),
        LatLng(2.3500, 102.8000),
        LatLng(1.6500, 103.6000),
      ],
      "Penang": [
        LatLng(5.4141, 100.3288),
        LatLng(5.3567, 100.2965),
        LatLng(5.3331, 100.3066),
        LatLng(5.4361, 100.3116),
        LatLng(5.2934, 100.2838),
        LatLng(5.3800, 100.4000),
        LatLng(5.5000, 100.4500),
        LatLng(5.2500, 100.5000),
        LatLng(5.3567, 100.2965),
      ],
      "Kuala Lumpur": [
        LatLng(3.1579, 101.7123),
        LatLng(3.1488, 101.7133),
        LatLng(3.1176, 101.6770),
        LatLng(3.0766, 101.7118),
        LatLng(3.1213, 101.6536),
        LatLng(3.1800, 101.7000),
        LatLng(3.1000, 101.7500),
        LatLng(3.2000, 101.6200),
        LatLng(3.1500, 101.6800),
        LatLng(3.1300, 101.7200),
      ],
      "Selangor": [
        LatLng(3.0738, 101.5183),
        LatLng(3.0449, 101.4456),
        LatLng(2.9213, 101.6559),
        LatLng(3.1502, 101.6155),
        LatLng(3.2379, 101.6840),
        LatLng(3.0000, 101.7000),
        LatLng(2.9500, 101.7500),
        LatLng(3.1000, 101.4000),
        LatLng(3.3000, 101.3000),
        LatLng(2.8000, 101.6000),
      ],
      "Perak": [
        LatLng(4.5975, 101.0901),
        LatLng(4.8538, 100.7441),
        LatLng(4.3392, 101.1432),
        LatLng(4.2100, 100.6600),
        LatLng(4.0259, 101.0178),
        LatLng(4.6500, 101.1500),
        LatLng(5.0000, 100.5000),
        LatLng(4.1000, 101.2000),
        LatLng(4.4000, 101.0500),
      ],
      "Melaka": [
        LatLng(2.1934, 102.2494),
        LatLng(2.2500, 102.1500),
        LatLng(2.3000, 102.3000),
        LatLng(2.1500, 102.4200),
        LatLng(2.4000, 102.2000),
        LatLng(2.1500, 102.3500),
        LatLng(2.2000, 102.4500),
        LatLng(2.3500, 102.1000),
        LatLng(2.2800, 102.2500),
        LatLng(2.2300, 102.2000),
      ],
      "Negeri Sembilan": [
        LatLng(2.7247, 101.9388),
        LatLng(2.6500, 101.8500),
        LatLng(2.8200, 101.8500),
        LatLng(2.6500, 102.0000),
        LatLng(2.7500, 102.2000),
        LatLng(2.6000, 102.4000),
        LatLng(2.8500, 101.9500),
        LatLng(2.4500, 101.9000),
        LatLng(2.7000, 102.1000),
        LatLng(2.7800, 101.8800),
      ],
      "Pahang": [
        LatLng(3.8168, 103.3317),
        LatLng(3.4219, 101.7941),
        LatLng(4.4714, 101.3750),
        LatLng(3.4800, 102.4200),
        LatLng(3.9300, 102.0500),
        LatLng(3.2000, 102.5000),
        LatLng(3.5000, 103.0000),
        LatLng(3.7000, 102.3000),
        LatLng(4.1000, 102.0000),
        LatLng(3.8500, 103.1000),
      ],
      "Terengganu": [
        LatLng(5.3341, 103.1368),
        LatLng(4.2300, 103.4200),
        LatLng(5.6500, 102.5000),
        LatLng(4.7500, 103.2000),
        LatLng(5.1500, 102.9000),
        LatLng(5.4000, 103.0000),
        LatLng(5.3302, 103.1408),
        LatLng(4.9000, 103.3500),
        LatLng(4.7500, 103.4000),
        LatLng(5.5500, 102.7500),
      ],
      "Kelantan": [
        LatLng(6.1211, 102.2464),
        LatLng(5.8500, 102.1500),
        LatLng(5.7000, 102.4000),
        LatLng(5.5000, 102.0000),
        LatLng(5.2000, 102.2000),
        LatLng(6.0000, 102.3500),
        LatLng(5.9500, 102.0500),
        LatLng(5.7500, 102.2500),
        LatLng(5.4000, 102.1000),
        LatLng(6.1500, 102.1800),
      ],
      "Kedah": [
        LatLng(6.1248, 100.3678),
        LatLng(5.7483, 100.4851),
        LatLng(6.4000, 99.8500),
        LatLng(5.3500, 100.5800),
        LatLng(5.9000, 100.6500),
        LatLng(6.2000, 100.4000),
        LatLng(6.0500, 100.5500),
        LatLng(5.8000, 100.4500),
        LatLng(5.6000, 100.5000),
        LatLng(6.3100, 99.9200),
      ],
      "Perlis": [
        LatLng(6.4389, 100.1945),
        LatLng(6.5000, 100.2500),
        LatLng(6.6000, 100.2800),
        LatLng(6.3800, 100.1500),
        LatLng(6.4500, 100.3000),
        LatLng(6.4200, 100.2200),
        LatLng(6.4800, 100.1800),
        LatLng(6.5500, 100.2000),
        LatLng(6.4000, 100.2500),
        LatLng(6.4700, 100.1200),
      ],
      "Sabah": [
        LatLng(5.9804, 116.0735),
        LatLng(5.8402, 118.1171),
        LatLng(4.2446, 117.8912),
        LatLng(5.3300, 115.9300),
        LatLng(6.0083, 116.5406),
        LatLng(5.9000, 116.1500),
        LatLng(5.7500, 116.3000),
        LatLng(6.2000, 116.7000),
        LatLng(5.5000, 118.0000),
        LatLng(4.5000, 118.2000),
      ],
      "Sarawak": [
        LatLng(1.5574, 110.3440),
        LatLng(2.2872, 111.8305),
        LatLng(4.3985, 113.9882),
        LatLng(3.1600, 113.0300),
        LatLng(2.0100, 112.9400),
        LatLng(1.4800, 110.4500),
        LatLng(1.6000, 110.2000),
        LatLng(2.5000, 111.9000),
        LatLng(4.3500, 114.0000),
        LatLng(3.5000, 113.5000),
      ],
      "Labuan": [
        LatLng(5.2831, 115.2308),
        LatLng(5.3100, 115.2400),
        LatLng(5.2850, 115.2300),
        LatLng(5.3200, 115.2400),
        LatLng(5.2900, 115.2200),
      ],
      "Putrajaya": [
        LatLng(2.9264, 101.6964),
        LatLng(2.9400, 101.7100),
        LatLng(2.9100, 101.6800),
        LatLng(2.9500, 101.7200),
        LatLng(2.9300, 101.6700),
        LatLng(2.9000, 101.6900),
        LatLng(2.9600, 101.7000),
        LatLng(2.9200, 101.7300),
        LatLng(2.9450, 101.6850),
      ],
    };
    stateData.forEach((stateName, coords) {
      for (int i = 0; i < coords.length; i++) {
        String busyLevel = [
          "🟢 Low Traffic",
          "🟡 Moderate",
          "🔴 Very Busy",
        ][random.nextInt(3)];

        String fakeAddress =
            "No. ${random.nextInt(100) + 1}, Jalan $stateName, ${random.nextInt(90000) + 10000} $stateName";

        String fakePhone =
            "+60 1${random.nextInt(9)}-${random.nextInt(899) + 100} ${random.nextInt(8999) + 1000}";

        _recyclingStations.add({
          "id": "node_${idCounter++}",
          "name": "$stateName Branch #${i + 1}",
          "lat": coords[i].latitude,
          "lng": coords[i].longitude,
          "address": fakeAddress,
          "phone": fakePhone,
          "hours": random.nextBool() ? "Mon-Sat: 9am-6pm" : "24 Hours",
          "busy": busyLevel,
          "distance": 0.0,
        });
      }
    });

    setState(() {
      _allStations = List.from(_recyclingStations);
    });
  }

  void _showStationDetails(Map<String, dynamic> station) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: const Color(0xFF1E1E1E),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (context) {
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  margin: const EdgeInsets.only(bottom: 20),
                  decoration: BoxDecoration(
                    color: Colors.grey[700],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),

              Text(
                station["name"],
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 5),

              Row(
                children: [
                  Text(
                    "${station['distance']} km away • ",
                    style: TextStyle(color: Colors.grey[400], fontSize: 14),
                  ),
                  Text(
                    station['busy'],
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),

              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.pop(context);
                        _launchMapsUrl(station["lat"], station["lng"]);
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF4CD964),
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 15),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: const Text(
                        "NAVIGATE",
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 15),
                  Container(
                    decoration: BoxDecoration(
                      color: Colors.grey[800],
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: IconButton(
                      onPressed: () async {
                        final String phoneNumber =
                            station['phone']; // 获取你生成的假号码
                        final Uri launchUri = Uri(
                          scheme: 'tel',
                          path: phoneNumber,
                        );

                        if (await canLaunchUrl(launchUri)) {
                          await launchUrl(launchUri);
                        } else {
                          print('Can\'t make call ');
                        }
                      },
                      icon: const Icon(Icons.phone, color: Colors.white),
                      padding: const EdgeInsets.all(12),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 25),

              // 信息列表
              _buildInfoRow(
                label: "Address",
                value: station['address'],
                icon: Icons.location_on,
              ),
              _buildInfoRow(
                label: "Phone",
                value: station['phone'],
                icon: Icons.phone,
              ),
              _buildInfoRow(
                label: "Operating Hours",
                value: station['hours'],
                icon: Icons.access_time_filled,
              ),
              const SizedBox(height: 20),
            ],
          ),
        );
      },
    );
  }

  Widget _buildInfoRow({
    required String label,
    required String value,
    required IconData icon,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 15),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: Colors.grey[500], size: 20),
          const SizedBox(width: 15),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: const TextStyle(
                    color: Color(0xFF4CD964),
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  value,
                  style: const TextStyle(color: Colors.white, fontSize: 14),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Future<Uint8List> getBytesFromAsset(String path, int width) async {
    ByteData data = await rootBundle.load(path);
    ui.Codec codec = await ui.instantiateImageCodec(
      data.buffer.asUint8List(),
      targetWidth: width,
    );
    ui.FrameInfo fi = await codec.getNextFrame();
    return (await fi.image.toByteData(
      format: ui.ImageByteFormat.png,
    ))!.buffer.asUint8List();
  }

  void _loadCustomMarker() async {
    try {
      final Uint8List markerIcon = await getBytesFromAsset(
        'assets/images/recycle_bin.png',
        100,
      );
      customIcon = BitmapDescriptor.fromBytes(markerIcon);
      setState(() {});
    } catch (e) {
      print("Error loading marker: $e");
    }
  }

  Future<void> _sortStationsByDistance() async {
    LocationPermission permission = await Geolocator.requestPermission();
    if (permission == LocationPermission.denied) return;
    Position userPos = await Geolocator.getCurrentPosition(
      locationSettings: const LocationSettings(accuracy: LocationAccuracy.high),
    );

    _mapController?.animateCamera(
      CameraUpdate.newCameraPosition(
        CameraPosition(
          target: LatLng(userPos.latitude, userPos.longitude),
          zoom: 12.5,
        ),
      ),
    );

    setState(() {
      for (var station in _allStations) {
        double dist = Geolocator.distanceBetween(
          userPos.latitude,
          userPos.longitude,
          station["lat"],
          station["lng"],
        );
        station["distance"] = double.parse((dist / 1000).toStringAsFixed(1));
      }
      _onFilterChanged(_selectedFilter);
      _recyclingStations.sort(
        (a, b) => (a["distance"] as double).compareTo(b["distance"] as double),
      );
    });

    if (_recyclingStations.isNotEmpty) {
      final nearestStation = _recyclingStations[0];
      if (mounted) {
        Future.delayed(const Duration(milliseconds: 800), () {
          _showStationDetails(nearestStation);
        });
      }

      _mapController?.animateCamera(
        CameraUpdate.newCameraPosition(
          CameraPosition(
            target: LatLng(nearestStation['lat'], nearestStation['lng']),
            zoom: 17,
            tilt: 45,
          ),
        ),
      );
    }
  }

  void _onFilterChanged(String filter) {
    setState(() {
      _selectedFilter = filter;
      List<Map<String, dynamic>> temp = List.from(_allStations);
      if (filter == '24 Hours') {
        temp = temp.where((s) => s['hours'] == '24 Hours').toList();
      } else if (filter == '🟢 Low Traffic')
        temp = temp.where((s) => s['busy'].contains('Low')).toList();
      else if (filter == '< 10km')
        temp = temp.where((s) => (s['distance'] as double) < 10.0).toList();

      temp.sort(
        (a, b) => (a["distance"] as double).compareTo(b["distance"] as double),
      );
      _recyclingStations = temp;
    });
    if (_recyclingStations.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Oops! No stations found nearby. Try another filter.'),
          backgroundColor: Colors.redAccent,
          duration: Duration(seconds: 2),
        ),
      );
    }
  }

  Future<void> _launchMapsUrl(double lat, double lng) async {
    final Uri googleMapsUrl = Uri.parse(
      'https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=driving',
    );
    if (await canLaunchUrl(googleMapsUrl)) {
      await launchUrl(googleMapsUrl, mode: LaunchMode.externalApplication);
    }
  }

  Future<void> _requestLocationPermission() async {
    await Permission.location.request();
  }

  Future<void> _checkMyLocation() async {
    Position userPos = await Geolocator.getCurrentPosition(
      locationSettings: const LocationSettings(accuracy: LocationAccuracy.high),
    );
    _mapController?.animateCamera(
      CameraUpdate.newCameraPosition(
        CameraPosition(
          target: LatLng(userPos.latitude, userPos.longitude),
          zoom: 16,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    bool isDarkMode = Theme.of(context).brightness == Brightness.dark;

    if (_mapController != null) {
      if (isDarkMode) {
        _mapController!.setMapStyle(_darkMapStyle);
      } else {
        _mapController!.setMapStyle(null);
      }
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Map'), backgroundColor: Colors.green),
      body: Stack(
        children: [
          GoogleMap(
            padding: const EdgeInsets.only(
              top: 80.0,
              bottom: 100.0,
            ), // 把系统图标往中间推
            zoomControlsEnabled: false,

            onMapCreated: (controller) {
              _mapController = controller;

              if (isDarkMode) controller.setMapStyle(_darkMapStyle);
            },
            initialCameraPosition: CameraPosition(
              target: _usmLocation,
              zoom: 15.0,
            ),
            myLocationEnabled: true,
            myLocationButtonEnabled: false,
            markers: _recyclingStations.map((station) {
              return Marker(
                markerId: MarkerId(station["id"]),
                position: LatLng(station["lat"], station["lng"]),
                icon:
                    customIcon ??
                    BitmapDescriptor.defaultMarkerWithHue(
                      BitmapDescriptor.hueGreen,
                    ),
                onTap: () => _showStationDetails(station),
              );
            }).toSet(),
          ),

          // Filter Bar
          Positioned(
            top: 20,
            left: 0,
            right: 0,
            child: SizedBox(
              height: 50,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 10),
                itemCount: _filterOptions.length,
                itemBuilder: (context, index) {
                  final filterName = _filterOptions[index];
                  final isSelected = _selectedFilter == filterName;
                  return Padding(
                    padding: const EdgeInsets.only(right: 10),
                    child: ActionChip(
                      label: Text(
                        filterName,
                        style: TextStyle(
                          color: isSelected ? Colors.white : Colors.black,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      backgroundColor: isSelected ? Colors.green : Colors.white,
                      onPressed: () => _onFilterChanged(filterName),
                    ),
                  );
                },
              ),
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _checkMyLocation,
        backgroundColor: Colors.green,
        child: const Icon(Icons.location_searching),
      ),
    );
  }
}

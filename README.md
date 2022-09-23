# Speeding up smartphone-based Dew computing in-vivo experiments via an evolutionary algorithm
This is a project that implements an EA-based component for preparing smartphones for battery-driven tests.
The project contains Java sources of a component, which we intent to make part of the Motrol software-hardware platform that allow to run in-lab battery-driven tests including profiling, benchmarking and, recently added, distributed inference tests, using smartphone clusters.
The project also contains necesary code to simulate battery charging/discharging actions on different smartphones whose profiles can be found in zipped
folder called device_profile.zip
To run the algorithm, please type in the command line "java -jar preparationSmartphones.jar [INSTANCE]" where [INSTANCE] represents an instance of the EA problem, or a file name within the "instances" folder.

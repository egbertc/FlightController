Final Project: Connor Egbert

everything runs under FlightPlanMAIN.

it creates 4 airport/weather stations which connect to aviationweather.gov and download up to date
weather in xml format.

it parses the data into JSON then sends it via camel topic to a mediator class which then keeps track
of weather for all stations.

2 airplanes and flight plans are then created.
first flight is a twin engine plane flying from Midway to St. Louis
second flight is a single engine flying from O'Hare to Rhinelander, Wisconsin.

ATC is a singleton pulling from a flight plan request queue.
it varifies that requested plans are valid then stores the plan in a HashMap.
it then posts a reply to a reply que where the flight planner can pick up messages with its
certain ID to get confirmation on a successful plan.

flightplans utilize the State pattern to behave differently during different phases of flight.

the ATC entity utilizes a thread to keep track of all flighplans and states.

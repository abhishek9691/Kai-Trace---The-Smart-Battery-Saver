# Kai-Trace---The-Smart-Battery-Saver

Kai-Trace is an Android application that uses a simple algorithm aimed at improving battery life of wireless sensor nodes through sensor control at the software level. Kai-Trace does so by learning sensor (WiFi, brightness, bluetooth, data, etc) on/off times and tracking them in a B-tree like lightweight data structure to avoid memory consumption, and still efficiently learn what time of day a sensor  should be on/off.  Kai-Trace learns as it goes so that the more you use it, the better it becomes, and it attempts to sync with the user's habits. 

Simple averages were used to compute the on/off times for now since this is a prototype, but in the future, simple Machine Learning algorithms could be applied, or other computation could be used to advance this. This prototype alone indicated a 10% increase in battery life with the test phone. 

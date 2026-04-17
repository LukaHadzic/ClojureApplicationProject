# Short description of project

This project is simulation of football match and it represents game logic that I will use for my next, bigger project including SpringBoot and Kafka. It covers basic events that can happen in football match - pass, offside, duel, cross, foul, penalty, red card, yellow card, shot, save, corner, out and goal out. For every event - there is some logic behind to decide how will event happen, based on match state and player's attributes. 

Main goal of project was to learn how to make probability-based game that will have as much logical flow as possible. The biggest point wasn't in some algorithmic optimisation or creating players that will have their own logic, but to make this simulation from nothing and be able to make more than 70 functions work and communicate with each other without creating a single non-consistent state. Also, big part of this project consists in writing tests and detailed documentation and explaination for every single of those functions.

Simulation is not made just to be event based (event happening after another), but with Poisson's probability model instead - time between events is calculated by statistical formulas. Of course, events are not instant and they also have their duration which is calculated by type of an event and also event's details (it's not the same duration if two equaly strong players are fighting in duel for the ball possession or one strong and one weak player). This makes simulation even more realistic.

Dynamics of simulation could be easily tuned, by changing lambda values for Poisson's model and also event's duration times based of event's details. Lambda values are divided by pitch zones what makes possible to tune dynamics in different pitch zones. Pass success rate, goal and yellow/red card rates could also be easily tuned, by simply changing corresponding values in maps.

Players have various of attributes that represent how well they do certain things. How will duel, shot, pass or save events end depends on values of event-relevant attributes of players evolved in that event.
Simulation can be easily upgraded by adding various how phisical but also mental/character attributes that will have their role in the game logic (f.e. higher the :aggressivity attribute, higher the chance for foul is).

At the end of simulation, all data required for match statistics are available to be calculated. Also, players have their own statistical attributes that are being updated during simulation in order to calculate and track players stats through matches.

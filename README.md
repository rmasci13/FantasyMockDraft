# Fantasy Football Mock Draft Simulator

A console-based application that simulates a fantasy football draft with CPU-controlled teams that adapt their picks based on team needs and roster construction.

## Technologies
- Java
- CSV parsing for player data management
- Object-oriented design

## Features
- Dynamic CPU drafting logic that adjusts player valuations
- Real-time CPU roster analysis that influences simulation pick decisions
- Position-based team needs calculation
- CSV-based player database with rankings

## Installation
1. Clone this repository
2. Ensure Java 11+ is installed on your system
3. Import project into your IDE of choice
4. Must retrieve player rankings CSV from FantasyPros website for updated rankings

## Usage
Run the FantasyMockDraft class to start the application. Follow the prompts to:
1. Set up number of teams, desired draft position, and number of rounds
2. Make selections when it's your turn
3. View CPU team selections in real-time

Can skip set-up step with the usage: java FantasyMockDraft [# of teams] [Draft position] [# of rounds]

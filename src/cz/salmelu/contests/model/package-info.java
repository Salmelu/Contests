/**
 * Holds all the objects used for data representation.<br>
 * It contains all data and information holders, namely {@link Contest} representing each individual contest
 * holding all of its data, {@link Category} representing a contest's category, {@link Discipline} representing
 * a discipline in which the contestants compete, {@link Contestant} and {@link TeamContestant} to represent
 * individual contestants, {@link TeamCategory} to represent different team categories for team race and 
 * {@link Team} to represent each team.<br>
 * There also is a {@link ContestInfo} class to allow sending contest's summary from the server to client and
 * {@link ScoreMode} to represent different methods to calculate team's score.<br>
 * There also are 2 helper classes: {@link IdFactory} used for handling each object's unique ids and to allow 
 * a safe and easy serializing of the current state; and {@link DataLoader} which handles all the serialization
 * and data storing.<br>
 * The classes in this package are used for data storage on server, temporary data storage on client, and for
 * transferring all the data over the sockets.
 * @author salmelu
 */
package cz.salmelu.contests.model;
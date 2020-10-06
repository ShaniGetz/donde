/*
Updates (old to new):
06/10/20,   16:43,  alon

*/

*General comments*
- Collections are named using (upper) CamelCase (e.g. 'Users', 'Events')
- Document fields are named using (lower) camelCase, starting with the collection name (e.g.
'eventName', 'userEmail')
- Notice that (sub-) collections that are in documents are named using (upper) CamelCase (e.g.
'EventInvitedUsers' sub-collection inside an event document)



*Firestore*

-   Events   [Collection]
-   -   eventName           [String]
-   -   eventDescription    [String]
-   -   eventCreatorID      [String]
-   -   eventCreatorName    [String]
-   -   eventTimeCreated    [Timestamp]
-   -   eventTimeStarting   [Timestamp]
-   -   eventLocationName   [String]
-   -   eventLocation       [Geopoint]
-   -   EventInvitedUsers   [Collection]
-   -   -   eventInvitedUserID      [String]    //references user document ID in Users collection
-   -   -   eventInvitedUserEmail   [String]
-   -   -   eventInvitedUserName    [String]

-   Users   [Collection]
-   -   userName            [String]
-   -   userEmail           [String]
-   -   UserInvitedEvents   [Collection]
-   -   -   userInvitedEventID              [String]    //references event document ID in Events collection
-   -   -   userInvitedEventName            [String]
-   -   -   userInvitedEventLocationName    [String]
-   -   -   userInvitedEventCreatorName     [String]
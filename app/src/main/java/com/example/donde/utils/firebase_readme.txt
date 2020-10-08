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


*Firebase Authentication*

-   -

*Firebase Firestore*

-   Events   [Collection]
-   -   eventName           [String]
-   -   eventDescription    [String]
-   -   eventCreatorUID      [String]
-   -   eventCreatorName    [String]
-   -   eventTimeCreated    [Timestamp]
-   -   eventTimeStarting   [Timefstamp]
-   -   eventLocationName   [String]
-   -   eventLocation       [Geopoint]
-   -   EventInvitedUsers   [Collection]        //document ID corresponds to userID
-   -   -   eventInvitedUserID              [String]    //references user document ID in Users collection
-   -   -   eventInvitedUserStatus          [String]
-   -   -   eventInvitedUserCurrentLocation [Geopoint]
-   -   -   eventInvitedUserProfilePicURL   [String]
-   -   -   eventInvitedUserName            [String]
-   -   -   eventInvitedUserIsGoing         [boolean]

-   Users   [Collection]                    //corresponds to userID
-   -   userName            [String]
-   -   userID              [String]        //references user ID as it appears in Firebase Authentication
-   -   userEmail           [String]
-   -   userProfilePicURL   [String]        //URL for the profile pic inside Firebase Storage
-   -   userInvitedEventIDs [Array<String>]
-   -   DEPRECATED:
-   -   UserInteractedUsers [Collection]    //users that the user interacted with
-   -   -   userInteractedUserID    [String]    //references the userID field as it appears in Users
-   -   -   userInteractedUserName  [String]
-   -   -   userInteractedUserEmail [String]
-   -   UserInvitedEvents   [Collection]
-   -   -   userInvitedEventID              [String]    //references event document ID in Events collection
-   -   -   userInvitedEventName            [String]
-   -   -   userInvitedEventLocationName    [String]
-   -   -   userInvitedEventCreatorName     [String]
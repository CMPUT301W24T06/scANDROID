/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// OpenAI ChatGPT 2024, How to send notification when information in firestore changes?
const {onRequest} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions/logger");
const {onDocumentWritten} = require("firebase-functions/v2/firestore");
const {admin} = require("firebase-admin");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");

admin.initializeApp();

exports.monitorFieldChange = functions.firestore
    .document("Events/{eventID}")
    .onUpdate((change, context) => {
        const newData = change.after.data();
        const oldData = change.before.data();

        const milestoneSeries = "milestoneSeries";
        const organizerID = "organizerID";

        // Check if the specific field has changed
        if (newData[milestoneSeries.get(0)] !== oldData[milestoneSeries.get(0)]) {
            // The specific field has changed, perform actions here
            express.sendEventUpdateNotification = functions.firestore
              .document("Users/"+organizerID)
              .onUpdate((change, context) => {
                const eventData = change.after.data();
                const eventID = context.params.eventId;

                // Retrieve FCM token of the user associated with the event from Firestore
                const userFCMToken = eventData.userFCMToken;

                // Construct the notification payload
                const payload = {
                  notification: {
                    title: 'Event Update',
                    body: 'Your event has been updated',
                  },
                  data: {
                    eventId: eventID,
                    // Add any additional data you want to send with the notification
                  },
                };

                // Send the notification
                return admin.messaging().sendToDevice(userFCMToken, payload);
              });

        }
        return null;
    });

    exports.monitorFieldChange = functions.firestore
        .document('Events/{eventID}')
        .onUpdate((change, context) => {
            const newData = change.after.data();
            const oldData = change.before.data();
            const specificField = 'yourFieldName';

            if (newData[specificField] !== oldData[specificField]) {
                console.log(`Field ${specificField} changed in document ${context.params.documentId}`);

                // Call the sendEventUpdateNotification function
                return sendEventUpdateNotification(newData.userFCMToken);
            }

            return null;
        });

    function sendEventUpdateNotification(userFCMToken) {
        // Implement the notification sending logic here
        // You can use Firebase Admin SDK to send notifications
        // Example:
        admin.messaging().sendToDevice(userFCMToken, payload);

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

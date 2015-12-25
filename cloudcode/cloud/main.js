
Parse.Cloud.afterSave("Booking", function(request) {
	
	/** parse push notification */
	Parse.Push.send({
    	channels: ["Drivers"],
	    data: {	    	
	      alert: "New booking request from " + request.object.get('bookedBy'),
	      json : {latitude : request.object.get('destiLatitude'),
	  				longitude : request.object.get('destiLongitude'),
	  				from : request.object.get('from'),
	  				to : request.object.get('to'),
	  				bookingId : request.object.id,
	  				bookingStatus : request.object.get('status') }
	      }
	    }, { success: function() { 
	    	console.log("SUCCESSSSS!");
	    }, error: function(error) { 
	      console.log(error)
	    }
	});
});
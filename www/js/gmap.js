
function initMap() {
    //element to dump it in (id Map) and options
    var username = document.getElementById("email_value").innerHTML;
    //map options
    var options = {
        zoom: 16,

        //styling the map.
        styles: [{ "featureType": "administrative", "elementType": "all", "stylers": [{ "visibility": "on" }, { "lightness": 33 }] }, { "featureType": "landscape", "elementType": "all", "stylers": [{ "color": "#f2e5d4" }] }, { "featureType": "poi.park", "elementType": "geometry", "stylers": [{ "color": "#c5dac6" }] }, { "featureType": "poi.park", "elementType": "labels", "stylers": [{ "visibility": "on" }, { "lightness": 20 }] }, { "featureType": "road", "elementType": "all", "stylers": [{ "lightness": 20 }] }, { "featureType": "road.highway", "elementType": "geometry", "stylers": [{ "color": "#c5c6c6" }] }, { "featureType": "road.arterial", "elementType": "geometry", "stylers": [{ "color": "#e4d7c6" }] }, { "featureType": "road.local", "elementType": "geometry", "stylers": [{ "color": "#fbfaf7" }] }, { "featureType": "water", "elementType": "all", "stylers": [{ "visibility": "on" }, { "color": "#acbcc9" }] }]

        //	center:{lat:42.3601, lng:-71.0589}
    }
    infoWindow = new google.maps.InfoWindow;
    //new map
    var map = new google.maps.Map(document.getElementById('map'), options);


    //Get current location
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            initialLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
            map.setCenter(initialLocation);
            infoWindow.setPosition(initialLocation);
            infoWindow.setContent('You are here');
            infoWindow.open(map);
        });
    }
    /* Code for one marker at a time 
    // Add marker
    var marker = new google.maps.Marker({
        position:{lat:42.4668,lng:-70.9495},
        //map we want to add it to.
        map:map,
        icon:'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'
    });
    
    var infoWindow = new google.maps.InfoWindow({content:'<h1>You are here</h1>'});
    
    marker.addListener('click', function(){
        infoWindow.open(map, marker);
    });
    */

    //Arry of Markers 

    // var markers = [
    // 	{
    // 		coords:{lat:53.355282, lng:-6.170307},
    // 		content:'Clontarf East Public Toilet<br>\
    // 		<a href="https://goo.gl/maps/RArBW6eMYBA2">Get Directions</a>'
    // 	},
    // 	{	coords:{lat:52.8584, lng:-7.9300},
    // 		content:'Supervalu Sundrive Complex<br>\
    // 		<a href="https://goo.gl/maps/pK1THpDLfev">Get Directions</a>'
    // 	},
    // 	{	coords:{lat:53.325535, lng:-6.207056},
    // 		content:'Sandymount Public Toilet<br>\
    // 		<a href="https://goo.gl/maps/yTXHt7Mc4TL2">Get Directions</a>'
    // 	},
    // 	{	coords:{lat:53.579336, lng:-6.105054},
    // 		content:'South Beach Public Toilet<br>\
    // 		<a href="https://goo.gl/maps/L8ETMBt7cRA2">Get Directions</a>'
    // 	},
    // 	{	coords:{lat:53.340139, lng: -6.262659 },
    // 		content:'<h1>Location 5 </h1><br>\
    // 		<a href="https://goo.gl/maps/L8ETMBt7cRA2">Get Directions</a>'
    // 	},
    // 	{	coords:{lat:51.8584, lng:-8.9300},
    // 		content:'<h1>Location 6 </h1><br>\
    // 		<a href="https://goo.gl/maps/L8ETMBt7cRA2">Get Directions</a>'
    // 	},
    // 	{	coords:{lat:52.8584, lng:-6.9300},
    // 		content:'Location 7<br>\
    // 		<a href="https://goo.gl/maps/L8ETMBt7cRA2">Get Directions</a>'
    // 	},

    // ];

    //Corresponding to html for Add bathroom 
    var created_date = new Date();
    //var lat = document.getElementById("lat");
    //var lng = document.getElementById("lng");
    var title = document.getElementById("title");
    var rating = document.getElementById("rating");
    var btype = document.getElementById("btype");

    //var review

    var bathroom = [
        {
            lat: lat,
            lng: lng,
            title: title,
            content: '<a href = "https://google.com/maps/place/"' + lat + ',' + lng + '>Get Directions</a>',
            rating: '<div class="star-rating-display" data-rating="'+rating+'"></div>',
            btype: btype,
            created_date: created_date
        }
    ];

    //Loop through markers
    for (var i = 0; i < markers.length; i++) {
        //Add marker
        addMarker(markers[i]);
    }

    // addMarker({coords:{lat:42.3601, lng:-71.0589},iconImage:'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png',
    // 	content:'<h1>You are here </h1>'
    // });
    // addMarker({coords:{lat:42.8584, lng:-70.9300},content:'<h1>Location 1 </h1>'});
    // addMarker({coords:{lat:42.7762, lng:-71.0773},content:'<h1>Location 1 </h1>'});



    // Add Marker Function
    function addMarker(props) {
        var marker = new google.maps.Marker({

            position: props.coords,
            map: map,
            icon: "img/map-icon-test.png"
            //without icon image icon is undefined, not ideal
            //icon:props.iconImage

        });
        //Check for custom icon
        if (props.iconImage) {
            //set Icon Image
            marker.setIcon(props.iconImage);
        }

        //Check for content
        if (props.content) {
            var infoWindow = new google.maps.InfoWindow({
                content: props.content
            });

            marker.addListener('click', function () {
                infoWindow.open(map, marker);

            });
        }
    }

    //GET ALL MAP DATA
    $("#getAllMapData").click(function () {
        $.ajax("https://fast-garden-93601.herokuapp.com/api/maps", {
            data: { get_param: 'value' },
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                $.each(data, function (index, element) {
                    console.log(element);
                });
            }
        });
    });


    //GET ALL REVIEWS FOR USER
    $("#getAllReviews").click(function () {
        $.ajax("https://fast-garden-93601.herokuapp.com/api/reviews", {
            data: { get_param: 'value' },
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                $.each(data, function (index, element) {
                    if (element.username == username) {
                        console.log(element);
                    }
                });
            }
        });
    });



    //GET ALL REVIEWS
    $("#getAllReviews").click(function () {
        $.ajax("https://fast-garden-93601.herokuapp.com/api/reviews", {
            data: { get_param: 'value' },
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                $.each(data, function (index, element) {
                    console.log(element);
                });
            }
        });
    });


    //SUBMIT BATHROOM
    $("#bathroomData").click(function () {
        $.ajax("https://fast-garden-93601.herokuapp.com/api/bathrooms", {
            data: JSON.stringify(bathroom),
            accept: "application/json",
            contentType: "application/json",
            method: "POST",
            success: function () {
                alert("Day added");
                console.log(bathroom);
            },
            error: function () {
                alert("Not added");
            }
        });
    });


    //SUBMIT REVIEW
    $("#reviewData").click(function () {
        $.ajax("https://fast-garden-93601.herokuapp.com/api/review", {
            data: JSON.stringify(bathroom),
            accept: "application/json",
            contentType: "application/json",
            method: "POST",
            success: function () {
                alert("Day added");
                console.log(bathroom);
            },
            error: function () {
                alert("Not added");
            }
        });
    });
}
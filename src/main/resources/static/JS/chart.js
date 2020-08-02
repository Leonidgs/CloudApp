
$(function(){	

	var bar = new ProgressBar.Circle('#progressbar', {
	  color: "#6f42c1",
	  strokeWidth: 40,
	  trailWidth: 8,
	  easing: 'easeInOut',
	  duration: 1400,
	  text: {
	    autoStyleContainer: false
	  },
	  from: { color: "#20c997", width: 2 },
	  to: { color: '#dc3545', width: 10 },
	  // Set default step function for all animate calls
	  step: function(state, circle) {
	    circle.path.setAttribute('stroke', state.color);
	    circle.path.setAttribute('stroke-width', state.width);
	
	    var value = Math.round(circle.value() * 100);
	    
	    console.log(circle.value());
	    
	    if (value === 0) {
	      circle.setText('');
	    } else {
	      circle.setText(value);
	    }
	
	  }
	});
	bar.text.style.fontFamily = '"Raleway", Helvetica, sans-serif';
	bar.text.style.fontSize = '2rem';
	
	bar.animate(0.5);  // Number from 0.0 to 1.0

});
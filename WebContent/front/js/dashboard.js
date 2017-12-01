$(function(){
	var account = localStorage.getItem('account');
	account = JSON.parse(account);
	if (account === null) {
		location.href = 'login.html'
	}
	$('#navNameTxt').text(account.name)
	var events = account.contracts.concat(account.products)
	events = events.sort(function(a,b){
		  return new Date(b.date) - new Date(a.date);
	});
	events.forEach(function(n){
		newDashboardNotification(n);
	});
	
	function newDashboardNotification(n) {
		t = getNotificationTime(n);
		i = getNotificationIcon(n);
		href = getNotificationHref(n);
		$('<a></a>').attr('href',href)
					.addClass('list-group-item')
					.append(i)
					.append(t)
					.appendTo($('#notificationsGroup'));		
	}
	
	function getNotificationTime(n) {
		var tx;
		timeElapsedMinutes = Math.floor((new Date().getTime() - new Date(n.date))/60000)
		if (timeElapsedMinutes < 120)
			tx = timeElapsedMinutes + 'minutes ago';
		else if (timeElapsedMinutes < 1440 )
			tx = Math.floor(timeElapsedMinutes/60) + 'hours ago'
		else 
			tx = n.date
		return $('<span></span>').addClass('notification-time')
								.css('color','#911')
								.addClass('pull-right')
								.addClass('small')
								.append($('<em></em>').text(tx))
	}
	
	function getNotificationIcon(n) {

		if (n.confirmed) {
			return $('<i></i>').addClass('fa')
							   .addClass('fa-edit')
							   .text('  Contract ' + isContractSentOrReceived(account,n));
	    }
	
		if (n.description) {
			return $('<i></i>').addClass('fa')
			   				  .addClass('fa-shopping-bag')
			   				  .text('  Product received');
		}
	}
	
	function isContractSentOrReceived(account,contract) {
		return contract.accountFrom === account.id ? 'sent' : 'received' 
	}	
	
	function getNotificationHref(n) {
		if (n.confirmed)
			return 'contractInfo.html?cid=' + n.id			
		else if (n.description)
			return 'productInfo.html?pid=' + n.id
	}
});


$(function(){
	$('#bt').on('click',function(){
		uname = $('#uname').val()
		pw = $('#pw').val()
		data = {'uname':uname,
				'pw':pw }
		$.post('../../GetAccount',data,function(response){
			if (response.error) {
				$('#err').text(response.error);
			}
			else if (response.account) {
				localStorage.setItem('account', response.account);
				location.href = 'dashboard.html'
			}
		})
	})
});
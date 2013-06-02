$ ->
  $(document).foundation()

  $('#login-button').click (e) ->
    e.preventDefault()
    target = $(e.target)
    $.ajax
      type: 'POST'
      url: '/login'
      dataType: 'application/x-www-form-urlencoded'
      data: target.closest('form').serialize()
      success: (data) -> window.location.href = data
      error: -> target.css 'background-color':'red'

  $('#register-button').click (e) -> $(e.target).closest('form').submit()

  resize = -> $('.scroll').css 'height', window.innerHeight - 280
  $(document).resize -> resize()
  resize()



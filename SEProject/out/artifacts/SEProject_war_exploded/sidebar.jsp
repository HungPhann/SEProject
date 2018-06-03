<div class="wrapper">
    <!-- Sidebar Holder -->
    <nav id="sidebar" class="active">
        <div class="sidebar-header">
            <h3>Bootstrap Sidebar</h3>
            <strong>ICT</strong>
        </div>

        <ul class="list-unstyled components">
            <li class=<%=request.getRequestURL().toString().contains("Chat.jsp")?"active":""%>>
                <a href="/get-chat-room-list">
                    <i class="glyphicon glyphicon-comment"></i>
                </a>
            </li>
            <li class=<%=request.getRequestURL().toString().contains("Contact.jsp")?"active":""%>>
                <a href="/get-friend-list">
                    <i class="fa fa-address-book-o"></i>
                </a>
            </li>
            <li class=<%=request.getRequestURL().toString().contains("Request.jsp")?"active":""%>>
                <a href="/get-request-list">
                    <i class="icon fa fa-user-plus"></i>
                </a>
            </li>
            <li class=<%=request.getRequestURL().toString().contains("setting")?"active":""%>>
                <a href="#">
                    <i class="icon fa fa-ellipsis-h"></i>
                </a>
            </li>
        </ul>
    </nav>

    <!-- Page Content Holder -->

</div>
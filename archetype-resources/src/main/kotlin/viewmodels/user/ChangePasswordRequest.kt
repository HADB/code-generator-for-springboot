package ${package_name}.viewmodels.user

data class ChangePasswordRequest(
    var oldPassword: String,
    var newPassword: String
)

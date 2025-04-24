document.addEventListener("DOMContentLoaded", function () {
  let isAdminManagePage = document.getElementById("manageAccountBtn") !== null;

  function showFalseAnnounce() {
    Swal.fire({
      title: "Error!",
      text: "Please fill full information",
      icon: "error",
      confirmButtonText: "OK",
    });
  }

  function showTrueAnnounce() {
    Swal.fire({
      title: "Update successfully",
      text: "Product is updated",
      icon: "success",
      confirmButtonText: "OK",
    }).then((result) => {
      if (result.isConfirmed) {
        if (!isCreatePage) window.location.href = "admin-product.html";
        else window.location.href = "admin-manage-account.html";
      }
    });
  }

  function validateForm() {
    let password = document
      .querySelectorAll("input[type='password']")[0]
      .value.trim();
    let address = document.querySelector("input[type='text']").value.trim();
    let phone = document.querySelectorAll("input[type='text']")[1].value.trim();
    let newPassword = document
      .querySelectorAll("input[type='password']")[1]
      .value.trim();
    let enterAgainPassword = document
      .querySelectorAll("input[type='password']")[2]
      .value.trim();

    if (
      !password ||
      !address ||
      !phone ||
      (newPassword && !enterAgainPassword) ||
      (!newPassword && enterAgainPassword)
    ) {
      showFalseAnnounce();
      return false;
    }

    return true;
  }

  if (isAdminManagePage) {
    document
      .getElementById("manageAccountBtn")
      .addEventListener("click", function () {
        if (validateForm()) {
          showTrueAnnounce();
        }
      });
  }
});

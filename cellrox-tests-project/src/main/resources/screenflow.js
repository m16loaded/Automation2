$(document).ready(function () {

    $("#slider").slider({
        min: 10,
        max: 150,
        step: 10,
        value: 50,
        slide: function (event, ui) {
            $('#value').text(ui.value);
            $('.screenshot').each(function () {
                $(this).css("width", ui.value + "%");
            });
        }

    });
});
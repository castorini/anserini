from django.forms import DateInput

class FengyuanChenDatePickerInput(DateInput):
    template_name = 'widgets/datepicker.html'
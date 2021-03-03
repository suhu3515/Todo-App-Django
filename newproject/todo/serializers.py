from todo import models
from rest_framework import fields, serializers

class TodoSerializer (serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.todotasks
        fields = ['id', 'taskname']
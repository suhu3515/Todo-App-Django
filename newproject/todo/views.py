from django.views.decorators.csrf import csrf_exempt
from rest_framework import serializers
from rest_framework.serializers import Serializer
from .serializers import TodoSerializer
from django.http.response import HttpResponse, JsonResponse
from .models import todotasks
from django.shortcuts import render
from rest_framework import viewsets, permissions
from rest_framework.parsers import JSONParser
# Create your views here.

def view(request):
    if request.method == "GET":
        tasks = todotasks.objects.all()
        serializer = TodoSerializer(tasks, many=True)
        return JsonResponse(serializer.data, safe=False,)

@csrf_exempt
def create(request):
    if request.method == 'POST':
        task_name = request.POST.get('task_name')
        tasks = todotasks(taskname=task_name)
        tasks.save()
        details = {'error':False}
        return JsonResponse(details, safe=False)

@csrf_exempt
def update(request, pk):
    try:
        if request.method == 'POST':
            tasks = todotasks.objects.get(pk=int(pk))
            task_name = request.POST.get('task_name')
            tasks.taskname=task_name
            tasks.save()
            details = {'error':False}
            return JsonResponse(details, safe=False)
    except todotasks.DoesNotExist:
        details = {'error':True}
        return JsonResponse(details, safe=False)


def delete(request):
    if request.method == "GET":
        tid = int(request.GET.get('tid'))
        tasks = todotasks.objects.get(pk=tid)
        tasks.delete()
        details = {'error':False}
        return JsonResponse(details, safe=False)
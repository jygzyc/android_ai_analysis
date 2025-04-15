#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
This module provides a set of tools for interacting with the JADX decompiler through
its HTTP API, allowing AI assistants to analyze and navigate Android application code.
"""

import sys
import httpx
from typing import List, Optional
from mcp.server.fastmcp import FastMCP

# Default JADX server address if not provided as command line argument
DEFAULT_JADX_SERVER = "http://127.0.0.1:8080/api/jadx"
# Get JADX server URI from command line or use default
jadx_server_uri = sys.argv[1].rstrip('/') if len(sys.argv) > 1 else DEFAULT_JADX_SERVER

# Initialize the MCP server with a descriptive name
mcp = FastMCP("Jadx MCP Server")


async def get_from_jadx(endpoint: str, params: dict = None) -> Optional[str]:
    """
    Send an asynchronous GET request to the JADX server.
    
    Args:
        endpoint: The API endpoint to call on the JADX server
        params: Optional query parameters to include in the request
        
    Returns:
        The response text or an error message if the request fails
    """
    try:
        async with httpx.AsyncClient() as client:
            resp = await client.get(
                f"{jadx_server_uri}/{endpoint}", 
                params=params, 
                timeout=10
            )
            resp.raise_for_status()
            return resp.text
    except Exception as e:
        return f"Error: {e}"


def get_from_jadx_sync(endpoint: str, params: dict = {}) -> Optional[str]:
    """
    Send a synchronous GET request to the JADX server.
    
    Args:
        endpoint: The API endpoint to call on the JADX server
        params: Optional query parameters to include in the request
        
    Returns:
        The response text or an error message if the request fails
    """
    try:
        with httpx.Client() as client:
            resp = client.get(
                f"{jadx_server_uri}/{endpoint}",
                params=params, 
                timeout=10
            )
            resp.raise_for_status()
            return resp.text
    except Exception as e:
        return f"Error: {e}"


async def post_to_jadx(endpoint: str, json_data: dict = None) -> Optional[str]:
    """
    Send an asynchronous POST request with JSON data to the JADX server.
    
    Args:
        endpoint: The API endpoint to call on the JADX server
        json_data: Optional JSON data to include in the request body
        
    Returns:
        The response text or an error message if the request fails
    """
    try:
        async with httpx.AsyncClient() as client:
            resp = await client.post(
                f"{jadx_server_uri}/{endpoint}",
                json=json_data,
                timeout=10
            )
            resp.raise_for_status()
            return resp.text
    except Exception as e:
        return f"Error: {e}"


@mcp.tool(name="get_method_code", description="Retrieve the source code of a specific method from a class.")
async def get_method_code(class_name: str, method_name: str) -> str:
    return await get_from_jadx("get_method_code", {"class": class_name, "method": method_name})


@mcp.tool(name="get_all_classes", description="Get a complete list of all classes in the decompiled project.")
async def get_all_classes() -> List[str]:
    response = await get_from_jadx("get_all_classes")
    return response.splitlines() if response else []


@mcp.tool(name="get_class_code", description="Retrieve the complete Java source code of a specified class.")
async def get_class_code(class_name: str) -> str:
    return await get_from_jadx("get_class_code", {"class": class_name})


@mcp.tool(name="search_method_by_name", description="Search for methods with a specific name across all classes in the project.")
async def search_method_by_name(method_name: str) -> List[str]:
    response = await get_from_jadx("search_method_by_name", {"method": method_name})
    return response.splitlines() if response else []


@mcp.tool(name="get_methods_of_class", description="List all methods defined in a specific class.")
async def get_methods_of_class(class_name: str) -> List[str]:
    response = await get_from_jadx("get_methods_of_class", {"class": class_name})
    return response.splitlines() if response else []


@mcp.tool(name="get_fields_of_class", description="List all fields and their types defined in a specific class.")
async def get_fields_of_class(class_name: str) -> List[str]:
    response = await get_from_jadx("get_fields_of_class", {"class": class_name})
    return response.splitlines() if response else []


@mcp.tool(name="get_smali_of_class", description="Retrieve the Smali (disassembled Dalvik bytecode) representation of a class.")
async def get_smali_of_class(class_name: str) -> str:
    return await get_from_jadx("get_smali_of_class", {"class": class_name})


@mcp.tool(name="get_implementation_of_interface", description="Find all classes that implement a specific interface.")
async def get_implementation_of_interface(interface_name: str) -> List[str]:
    response = await get_from_jadx("get_implementation_of_interface", {"interface": interface_name})
    return response.splitlines() if response else []


@mcp.tool(name="get_superclasses_of_class", description="Get the inheritance hierarchy (parent classes) of a specific class.")
async def get_superclasses_of_class(class_name: str) -> List[str]:
    response = await get_from_jadx("get_superclasses_of_class", {"class": class_name})
    return response.splitlines() if response else []


@mcp.tool(name="find_xref_of_method", description="Find all cross-references (usages) of a specific method.")
async def find_xref_of_method(class_name: str, method_name: str) -> List[str]:
    response = await get_from_jadx("find_xref_of_method", {"class": class_name, "method": method_name})
    return response.splitlines() if response else []


if __name__ == "__main__":
    # Start the MCP server using standard input/output for communication
    mcp.run(transport="stdio")
